package com.hedvig.underwriter.config

import com.github.imifou.jsonschema.module.addon.AddonModule
import com.github.victools.jsonschema.generator.OptionPreset
import com.github.victools.jsonschema.generator.SchemaGenerator
import com.github.victools.jsonschema.generator.SchemaGeneratorConfigBuilder
import com.github.victools.jsonschema.generator.SchemaVersion
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class JsonSchemaConfig {
    @Bean
    fun jsonSchemaGenerator(): SchemaGenerator {
        val configBuilder =
            SchemaGeneratorConfigBuilder(SchemaVersion.DRAFT_7, OptionPreset.PLAIN_JSON)
                .with(AddonModule())
        disableSorting(configBuilder)
        setClassNameAsId(configBuilder)
        setupDefaults(configBuilder)
        val config = configBuilder.build()
        return SchemaGenerator(config)
    }

    private fun disableSorting(configBuilder: SchemaGeneratorConfigBuilder) {
        configBuilder.forTypesInGeneral().withPropertySorter { _, _ -> 0 }
    }

    private fun setClassNameAsId(configBuilder: SchemaGeneratorConfigBuilder) {
        configBuilder.forTypesInGeneral().withIdResolver { typeScope ->
            val isClass = typeScope.javaClass.simpleName == "TypeScope"
            if (!isClass) {
                return@withIdResolver null
            }
            val className = typeScope.simpleTypeDescription
            return@withIdResolver className
        }
    }

    private fun setupDefaults(configBuilder: SchemaGeneratorConfigBuilder) {
        configBuilder.forTypesInGeneral().withDefaultResolver { typeScope ->
            return@withDefaultResolver when {
                typeScope.type.typeName.contains("java.util.List") -> emptyList<Any>()
                typeScope.type.typeName == "boolean" -> false
                else -> null
            }
        }
    }
}
