package com.hedvig.underwriter.config

import javax.sql.DataSource
import liquibase.Liquibase
import liquibase.database.DatabaseFactory
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.ClassLoaderResourceAccessor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
class LiquibaseConfig {
    @Bean
    @Primary
    fun hedvigsLiquibase(dataSource: DataSource): Liquibase {
        val database = DatabaseFactory.getInstance()
            .findCorrectDatabaseImplementation(JdbcConnection(dataSource.connection))
        return Liquibase("db/changelog/changelog-master.yml", ClassLoaderResourceAccessor(), database)
    }
}
