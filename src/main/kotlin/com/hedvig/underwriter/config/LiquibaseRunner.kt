package com.hedvig.underwriter.config

import liquibase.Contexts;
import liquibase.Liquibase
import org.springframework.boot.ApplicationArguments
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
class LiquibaseRunner(private val liquibase: Liquibase, private val applicationArguments: ApplicationArguments) {
    @PostConstruct
    fun init() {
        if (applicationArguments.optionNames.contains("liquibase-update")) {
            liquibase.update(Contexts())
        }

        if (applicationArguments.optionNames.contains("liquibase-rollback-1")) {
            liquibase.rollback(1, "")
        }
    }
}
