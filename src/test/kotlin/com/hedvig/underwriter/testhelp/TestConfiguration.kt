package com.hedvig.underwriter.testhelp

import com.hedvig.underwriter.testhelp.EmbeddedPostgresSingleton.embeddedPostgres
import javax.sql.DataSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class TestConfiguration {
    @Bean
    fun embeddedDataSource(): DataSource =
        embeddedPostgres.postgresDatabase
}
