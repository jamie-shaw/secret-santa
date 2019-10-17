package com.secretsanta.api.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
public class AccessDatasourceConfig {

//    @Bean
//    public DataSource accessDataSource() {
//        DriverManagerDataSource dataSource = new DriverManagerDataSource();
//        dataSource.setDriverClassName("net.ucanaccess.jdbc.UcanaccessDriver");
//        dataSource.setUrl("jdbc:ucanaccess://D:/My Documents/Software/Development/Secret Santa/source/data/santa.mdb");
//        
//        return dataSource;
//    }
}
