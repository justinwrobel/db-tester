package com.laur;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DbTesterApplication {

	/**
	 * java -Ddbt.logging=DEBUG -Ddbt.config="C:\\dbt-config.properties" -jar
	 * 
	 * dbt.properties:
	 * 
	 * 
	 * dbt.sql = select 2 as "default" from dual
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) {
		try {

			log.debug("Starting");
			
			printVersion();

			String configFile = System.getProperty("dbt.config");

			String defaultConfigLocation = "dbt-config.properties";
			configFile = configFile == null || configFile.isEmpty() ? defaultConfigLocation : configFile;

			// find supplied config file
			if (configFile != null && !configFile.isEmpty()) {
				File file = new File(configFile);
				if (!file.exists()) {
					InputStream is = DbTesterApplication.class.getResourceAsStream("/default.properties");
					log.info("Intializing config at {}. Please customize and re-run.", file.getAbsolutePath());
					FileUtils.copyInputStreamToFile(is, file);

					System.exit(0);
				}
			}

			ApplicationContext context = new ClassPathXmlApplicationContext("/application-context.xml");
			DbTesterQuerier querier = context.getBean(DbTesterQuerier.class);
			List<Map<String, Object>> results = querier.runQuery();

			log.debug("results{}", results);

			// Display results, one result per line, with commas between items,
			// with colons between key and values
			for (Map<String, Object> map : results) {
				StringBuilder sb = new StringBuilder();
				for (Iterator<Entry<String, Object>> i = map.entrySet().iterator(); i.hasNext();) {
					Entry<String, Object> entry = i.next();
					sb.append(entry.getKey()).append(": ");
					sb.append(entry.getValue());
					sb.append(i.hasNext() ? "," : "");
				}
				log.info("{}", sb);
			}

			((ConfigurableApplicationContext) context).close();
		} catch (Exception e) {
			log.error("{}", e.getMessage());
			if(log.isDebugEnabled()) log.error("STACKTRACE", e);
		}
	}

	private static void printVersion() throws IOException {
		Properties prop = new Properties();
		InputStream in = DbTesterApplication.class.getResourceAsStream("/META-INF/maven/com.laur/db-tester/pom.properties");
		prop.load(in);
		in.close();
		
		log.debug("Version: {}",prop.get("version"));
	}

}
