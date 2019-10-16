package com.hedvig.underwriter.testhelp

import com.hedvig.underwriter.testhelp.EmbeddedPostgresSingleton.embeddedPostgres
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@Configuration
class TestConfiguration {
    @Bean
    fun embeddedDataSource(): DataSource =
        embeddedPostgres.postgresDatabase
}
