package com.cool.app.jobconfig;


import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.cool.app.itemprocessor.DataProcessor;
import com.cool.app.joblisteners.JobCompletionNotificationListener;
import com.cool.domain.DataDTO;

@EnableBatchProcessing
@Configuration
public class CsvFileToDatabaseConfig {
	
    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    public DataSource dataSource;
    
    // begin reader, writer, and processor

    
    @Bean
    public FlatFileItemReader<DataDTO> csvAnimeReader(){
        FlatFileItemReader<DataDTO> reader = new FlatFileItemReader<DataDTO>();
        reader.setResource(new ClassPathResource("datacsv.csv"));
        reader.setLineMapper(new DefaultLineMapper<DataDTO>() {{
            setLineTokenizer(new DelimitedLineTokenizer() {{
                setNames(new String[] { "id", "title", "description" });
            }});
            setFieldSetMapper(new BeanWrapperFieldSetMapper<DataDTO>() {{
                setTargetType(DataDTO.class);
            }});
        }});
        return reader;
    }


	@Bean
	ItemProcessor<DataDTO, DataDTO> csvAnimeProcessor() {
		return new DataProcessor();
	}

	@Bean
	public JdbcBatchItemWriter<DataDTO> csvAnimeWriter() {
		 JdbcBatchItemWriter<DataDTO> csvAnimeWriter = new JdbcBatchItemWriter<DataDTO>();
		 csvAnimeWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<DataDTO>());
		 csvAnimeWriter.setSql("INSERT INTO animes ( title, description) VALUES ( :title, :description)");
		 csvAnimeWriter.setDataSource(dataSource);
	        return csvAnimeWriter;
	}

	 // end reader, writer, and processor

    // begin job info
	@Bean
	public Step csvFileToDatabaseStep() {
		return stepBuilderFactory.get("csvFileToDatabaseStep")
				.<DataDTO, DataDTO>chunk(1)
				.reader(csvAnimeReader())
				.processor(csvAnimeProcessor())
				.writer(csvAnimeWriter())
				.build();
	}

	@Bean
	Job csvFileToDatabaseJob(JobCompletionNotificationListener listener) {
		return jobBuilderFactory.get("csvFileToDatabaseJob")
				.incrementer(new RunIdIncrementer())
				.listener(listener)
				.flow(csvFileToDatabaseStep())
				.end()
				.build();
	}
	 // end job info
}
