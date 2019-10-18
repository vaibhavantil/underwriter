package com.hedvig.underwriter.config

import javax.sql.DataSource
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.jdbi.v3.postgres.PostgresPlugin
import org.jdbi.v3.sqlobject.SqlObjectPlugin
import org.jdbi.v3.sqlobject.kotlin.KotlinSqlObjectPlugin
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class JdbiConfig {
    @Bean
    fun jdbi(dataSource: DataSource): Jdbi = Jdbi.create(dataSource).install()
}

fun Jdbi.install(): Jdbi = this.installPlugin(PostgresPlugin())
    .installPlugin(KotlinPlugin())
    .installPlugin(KotlinSqlObjectPlugin())
    .installPlugin(SqlObjectPlugin())
