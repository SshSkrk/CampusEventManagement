package sasha.org.campuseventmanagement.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.net.URI;

@Configuration
public class DataSourceConfig {

    @Value("${DATABASE_URL:}")
    private String databaseUrl; // empty string if not set

    @Bean
    public DataSource dataSource() throws Exception {
        if (databaseUrl == null || databaseUrl.isEmpty()) {
            // Local fallback: H2 database
            return DataSourceBuilder.create()
                    .url("jdbc:h2:mem:campusdb")
                    .username("sa")
                    .password("")
                    .driverClassName("org.h2.Driver")
                    .build();
        }

        // Heroku PostgreSQL
        URI dbUri = new URI(databaseUrl);
        String username = dbUri.getUserInfo().split(":")[0];
        String password = dbUri.getUserInfo().split(":")[1];
        String jdbcUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();

        return DataSourceBuilder.create()
                .url(jdbcUrl)
                .username(username)
                .password(password)
                .driverClassName("org.postgresql.Driver")
                .build();
    }
}
