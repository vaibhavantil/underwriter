package com.hedvig.underwriter.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.zalando.jackson.datatype.money.MoneyModule

@Configuration
class Money {
    @Bean
    fun monetaModule() =
        MoneyModule().withQuotedDecimalNumbers()
}
