/*package com.shilin.gulimall.order.config;

import com.zaxxer.hikari.HikariDataSource;
import io.seata.rm.datasource.DataSourceProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;

*//**
 * @author shilin
 * @email g1335026358@gmail.com
 * @date 2020-12-04 15:51:30
 *//*
//@Configuration
public class MySeataConfig {
    @Autowired
    private DataSourceProperties dataSourceProperties;

        @Bean
    public DataSource dataSource() {
        HikariDataSource dataSource = dataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
        if (StringUtils.hasText(dataSourceProperties.getName())) {
            dataSource.setPoolName(dataSourceProperties.getName());
        }
        return new DataSourceProxy(dataSource);
    }
}*/
