package com.cool.app.joblisteners;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.cool.domain.DataDTO;

@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

	private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

	private final JdbcTemplate jdbcTemplate;

	@Autowired
	public JobCompletionNotificationListener(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		if(jobExecution.getStatus() == BatchStatus.COMPLETED) {
			log.info("============ JOB FINISHED ============ Verifying the results....\n");

			List<DataDTO> results = jdbcTemplate.query("SELECT id, title, description FROM animes", new RowMapper<DataDTO>() {
				@Override
				public DataDTO mapRow(ResultSet rs, int row) throws SQLException {
					return new DataDTO(rs.getString(1), rs.getString(2), rs.getString(3));
				}
			});

			for (DataDTO AnimeDTO : results) {
				log.info("Discovered <" + AnimeDTO + "> in the database.");
			}

		}
	}
	
}
