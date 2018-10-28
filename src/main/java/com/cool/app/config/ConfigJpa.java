
package com.cool.app.config;


import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.instrument.classloading.InstrumentationLoadTimeWeaver;
import org.springframework.orm.hibernate4.HibernateExceptionTranslator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;


@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages="com.cool.dao")
@PropertySource("classpath:db.properties")
public class ConfigJpa 
{
	@Autowired
	private Environment env;

	
	@Bean
	public PlatformTransactionManager transactionManager()
	{
		EntityManagerFactory factory = entityManagerFactory().getObject();
		return new JpaTransactionManager(factory);
	}
 
	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory()
	{
		LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();

		HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		vendorAdapter.setGenerateDdl(Boolean.TRUE);
		vendorAdapter.setShowSql(Boolean.TRUE);

		factory.setDataSource(getDataSource());
		factory.setJpaVendorAdapter(vendorAdapter);
		factory.setPackagesToScan("com.cool.domain");
  		factory.setJpaProperties(getHibernateProperties());
 		factory.afterPropertiesSet();
		factory.setLoadTimeWeaver(new InstrumentationLoadTimeWeaver());
		return factory;
	}

	@Bean
	public HibernateExceptionTranslator hibernateExceptionTranslator()
	{
		return new HibernateExceptionTranslator();
	}
	 
	@Bean(name = "dataSource")
	public DataSource getDataSource() {
	 
	  HikariConfig config = new HikariConfig();
		config.setJdbcUrl(env.getRequiredProperty("db.url"));
		config.setDriverClassName(env.getRequiredProperty("db.driver"));
		config.setUsername(env.getRequiredProperty("db.username"));
		config.setPassword(env.getRequiredProperty("db.password"));
	 	config.setConnectionTestQuery(env.getRequiredProperty("db.hikari.jdbc.validationQuery"));
		config.setAutoCommit(Boolean.getBoolean(env.getRequiredProperty("db.hikari.autoCommit")));
		config.setMaximumPoolSize(Integer.parseInt(env.getRequiredProperty("db.hikari.maximumPoolSize")));
		config.setMinimumIdle(Integer.parseInt(env.getRequiredProperty("db.hikari.minimumIdle")));
		config.setIdleTimeout(Long.parseLong(env.getRequiredProperty("db.hikari.idleTimeout")));
		config.setMaxLifetime(Long.parseLong(env.getRequiredProperty("db.hikari.maxLifetime")));
	 	config.setPoolName(env.getRequiredProperty("db.hikari.poolName"));
	 	config.setValidationTimeout(TimeUnit.MINUTES.toMillis(1));
		config.setLeakDetectionThreshold(5000);
		return new HikariDataSource(config);
			
	   }
	   private Properties getHibernateProperties() {
			Properties properties = new Properties();
			properties.put("hibernate.show_sql",env.getRequiredProperty("hibernate.show_sql"));
			properties.put("hibernate.dialect",env.getRequiredProperty("hibernate.dialect"));
			properties.put("hibernate.hbm2ddl.auto",env.getRequiredProperty("hibernate.hbm2ddl.auto"));
			return properties;
		}
	
 }