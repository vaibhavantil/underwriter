package com.hedvig.underwriter.config


import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.jdbi.v3.postgres.PostgresPlugin
import org.jdbi.v3.sqlobject.SqlObjectPlugin
import org.jdbi.v3.sqlobject.kotlin.KotlinSqlObjectPlugin
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource


@Configuration
class JdbiConfig (
    private val dataSource: DataSource
) {
    @Bean
    fun jdbi(): Jdbi {
        return Jdbi.create(dataSource)
            .installPlugin(PostgresPlugin())
            .installPlugin(KotlinPlugin())
            .installPlugin(KotlinSqlObjectPlugin())
            .installPlugin(SqlObjectPlugin())
            //.registerColumnMapper(MoneyMapper())
            //.registerArgument(MoneyArgumentFactory())
            //.registerColumnMapper(MonthMapper())
            //.registerArgument(MonthArgumentFactory())
            //.registerColumnMapper(YearMapper())
            //.registerArgument(YearArgumentFactory())
    }
}