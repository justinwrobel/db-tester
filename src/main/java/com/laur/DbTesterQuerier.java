package com.laur;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DbTesterQuerier {
	
	private JdbcTemplate jdbcTemplate;
	
	@Value("${dbt.sql}")
	String sql;

	@Autowired
	public void setDataSource(DataSource dataSource) {
	    this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public List<Map<String, Object>> runQuery(){
		return jdbcTemplate.queryForList(sql);
	}

}
