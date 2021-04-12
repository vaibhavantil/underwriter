package com.hedvig.underwriter.config

import ch.qos.logback.access.tomcat.LogbackValve
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory
import org.springframework.boot.web.servlet.server.ServletWebServerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class LogBackAccess {

    @Bean
    fun servletContainer(): ServletWebServerFactory {
        val tomcat = TomcatServletWebServerFactory()

        val logbackValve = LogbackValve()

        // point to logback-access.xml
        logbackValve.filename = "logback-access.xml"

        tomcat.addContextValves(logbackValve)

        return tomcat
    }
}
