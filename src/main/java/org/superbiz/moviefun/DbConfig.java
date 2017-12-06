package org.superbiz.moviefun;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class DbConfig {
    @Bean
    public DataSource albumsDataSource(
            @Value("${moviefun.datasources.albums.url}") String url,
            @Value("${moviefun.datasources.albums.username}") String username,
            @Value("${moviefun.datasources.albums.password}") String password
    ) {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL(url);
        dataSource.setUser(username);
        dataSource.setPassword(password);
        return dataSource;
    }

    @Bean
    public DataSource moviesDataSource(
            @Value("${moviefun.datasources.movies.url}") String url,
            @Value("${moviefun.datasources.movies.username}") String username,
            @Value("${moviefun.datasources.movies.password}") String password
    ) {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL(url);
        dataSource.setUser(username);
        dataSource.setPassword(password);
        return dataSource;
    }

    @Bean
    HibernateJpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
        jpaVendorAdapter.setDatabase(Database.MYSQL);
        jpaVendorAdapter.setDatabasePlatform("org.hibernate.dialect.MySQL5InnoDBDialect");
        jpaVendorAdapter.setGenerateDdl(true);
        return jpaVendorAdapter;
    }

    private static DataSource buildDataSource(String url, String username, String password) {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL(url);
        dataSource.setUser(username);
        dataSource.setPassword(password);
        return createConnectionPool(dataSource);
    }


    private static LocalContainerEntityManagerFactoryBean buildEntityManagerFactoryBean(DataSource dataSource, HibernateJpaVendorAdapter jpaVendorAdapter, String unitName) {
        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setJpaVendorAdapter(jpaVendorAdapter);
        factoryBean.setPackagesToScan(DbConfig.class.getPackage().getName());
        factoryBean.setPersistenceUnitName(unitName);
        return factoryBean;
    }

    private static DataSource createConnectionPool(DataSource dataSource) {
        HikariConfig config = new HikariConfig();
        config.setDataSource(dataSource);
        return new HikariDataSource(config);
    }

    @Configuration
    public static class Movies {
        @Value("${moviefun.datasources.movies.url}")
        String url;
        @Value("${moviefun.datasources.movies.username}")
        String username;
        @Value("${moviefun.datasources.movies.password}")
        String password;

        @Bean
        public DataSource moviesDataSource() {
            return buildDataSource(url, username, password);
        }

        @Bean
        @Qualifier("movies")
        LocalContainerEntityManagerFactoryBean createMoviesFactory(DataSource moviesDataSource, HibernateJpaVendorAdapter hibernateAdapter) {
            return buildEntityManagerFactoryBean(moviesDataSource, hibernateAdapter, "movies");
        }

        @Bean
        @Qualifier("movies")
        PlatformTransactionManager createMoviesTransactionManager(@Qualifier("movies") LocalContainerEntityManagerFactoryBean factoryBean) {
            return new JpaTransactionManager(factoryBean.getObject());
        }
    }

    @Configuration
    public static class Albums {
        @Value("${moviefun.datasources.albums.url}")
        String url;
        @Value("${moviefun.datasources.albums.username}")
        String username;
        @Value("${moviefun.datasources.albums.password}")
        String password;

        @Bean
        public DataSource albumsDataSource() {
            return buildDataSource(url, username, password);
        }

        @Bean
        @Qualifier("albums")
        LocalContainerEntityManagerFactoryBean createAlbumsFactory(DataSource albumsDataSource, HibernateJpaVendorAdapter hibernateAdapter) {
            return buildEntityManagerFactoryBean(albumsDataSource, hibernateAdapter, "albums");
        }

        @Bean
        @Qualifier("albums")
        PlatformTransactionManager albumsTransactionManager(@Qualifier("albums") LocalContainerEntityManagerFactoryBean factoryBean) {
            return new JpaTransactionManager(factoryBean.getObject());
        }
    }
}
