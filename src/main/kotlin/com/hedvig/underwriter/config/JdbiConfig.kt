package com.hedvig.underwriter.config

import com.fasterxml.jackson.databind.ObjectMapper
import javax.sql.DataSource
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.jdbi.v3.jackson2.Jackson2Config
import org.jdbi.v3.jackson2.Jackson2Plugin
import org.jdbi.v3.postgres.PostgresPlugin
import org.jdbi.v3.sqlobject.SqlObjectPlugin
import org.jdbi.v3.sqlobject.kotlin.KotlinSqlObjectPlugin
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class JdbiConfig {
    @Bean
    fun jdbi(dataSource: DataSource, objectMapper: ObjectMapper): Jdbi =
        Jdbi.create(dataSource).install()
            .apply {
                getConfig(Jackson2Config::class.java).mapper = objectMapper
            }
}

fun Jdbi.install(): Jdbi = this.installPlugin(PostgresPlugin())
    .installPlugin(KotlinPlugin())
    .installPlugin(KotlinSqlObjectPlugin())
    .installPlugin(SqlObjectPlugin())
    .installPlugin(Jackson2Plugin())
