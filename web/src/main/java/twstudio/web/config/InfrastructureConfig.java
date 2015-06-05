package twstudio.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import twstudio.domain.*;
import twstudio.infrastructure.repoImpl.ArticleRepoImpl;
import twstudio.infrastructure.repoImpl.ExternalFileRepoImpl;
import twstudio.infrastructure.repoImpl.PortalRepoImpl;
import twstudio.infrastructure.repoImpl.TopicRepoImpl;

import javax.sql.DataSource;


/**
 * Created by Tao on 5/23/2015.
 */

@Configuration
@EnableTransactionManagement
public class InfrastructureConfig {
    @Bean
    @Profile("dev")
    public DataSource devDatasource() {
        org.apache.tomcat.jdbc.pool.DataSource datasource = new org.apache.tomcat.jdbc.pool.DataSource();
        datasource.setDriverClassName("org.postgresql.Driver");
        datasource.setUrl("jdbc:postgresql://localhost:5432/refbook");
        datasource.setUsername("postgres");
        datasource.setPassword("rockslide");
        return datasource;
    }
    @Bean
    @Profile("prod")
    public DataSource prodDatasource() {
        org.apache.tomcat.jdbc.pool.DataSource datasource = new org.apache.tomcat.jdbc.pool.DataSource();
        datasource.setDriverClassName("org.postgresql.Driver");
        datasource.setUrl("jdbc:postgresql://localhost:5432/refbook");
        datasource.setUsername("postgres");
        datasource.setPassword("rockslide");
        return datasource;
    }

    @Bean
    public NamedParameterJdbcOperations namedJdbcTemplate(DataSource datasource){
       return new NamedParameterJdbcTemplate(datasource);
    }
    @Bean
    public PortalRepo portalRepo(NamedParameterJdbcOperations namedJdbcTemplate){
        PortalRepoImpl portalRepo = new PortalRepoImpl();
        portalRepo.setJdbcTemplate(namedJdbcTemplate);
        return portalRepo;
    }
    @Bean
    public TopicRepo topicRepo(DataSource datasource, NamedParameterJdbcOperations namedJdbcTemplate){
        TopicRepoImpl topicRepo = new TopicRepoImpl();
        topicRepo.setJdbcTemplate(namedJdbcTemplate);
        topicRepo.setDatasource(datasource);
        return topicRepo;
    }
    @Bean
    public ExternalFileRepo externalFileRepo(DataSource datasource, NamedParameterJdbcOperations namedJdbcTemplate){
        ExternalFileRepoImpl externalFileRepo = new ExternalFileRepoImpl();
        externalFileRepo.setJdbcTemplate(namedJdbcTemplate);
        externalFileRepo.setDatasource(datasource);
        return externalFileRepo;
    }
    @Bean
    public ArticleRepo articleRepo(DataSource datasource, NamedParameterJdbcOperations namedJdbcTemplate){
        ArticleRepoImpl articleRepo = new ArticleRepoImpl();
        articleRepo.setJdbcTemplate(namedJdbcTemplate);
        articleRepo.setDatasource(datasource);
        return articleRepo;
    }
    @Bean
    public PlatformTransactionManager transactionManager(DataSource datasource){
        return new DataSourceTransactionManager(datasource);
    }
}
