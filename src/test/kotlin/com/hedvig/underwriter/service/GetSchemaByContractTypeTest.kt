package com.hedvig.underwriter.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.hedvig.underwriter.config.JsonSchemaConfig
import com.hedvig.underwriter.model.ContractType
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

internal class GetSchemaByContractTypeTest {

    private val objectMapper = ObjectMapper()

    val schemaServiceToTest = QuoteSchemaServiceImpl(
        mockk(),
        mockk(),
        JsonSchemaConfig().jsonSchemaGenerator()
    )

    @Test
    fun `validate apartment schema`() {
        val swedishApartmentSchemaJson = """
            {
              "${'$'}schema": "http://json-schema.org/draft-07/schema#",
              "type": "object",
              "properties": {
                "lineOfBusiness": {
                  "type": "string",
                  "enum": [
                    "BRF",
                    "RENT",
                    "STUDENT_BRF",
                    "STUDENT_RENT"
                  ],
                  "title": "Line Of Business"
                },
                "street": {
                  "type": "string",
                  "title": "Street"
                },
                "zipCode": {
                  "type": "string",
                  "title": "Zip Code",
                  "minLength": 5,
                  "maxLength": 5
                },
                "city": {
                  "type": "string",
                  "title": "City"
                },
                "livingSpace": {
                  "type": "integer",
                  "title": "Living Space",
                  "minimum": 0
                },
                "numberCoInsured": {
                  "type": "integer",
                  "title": "Number Co-Insured",
                  "minimum": 0
                }
              },
              "required": [
                "lineOfBusiness",
                "street",
                "zipCode",
                "livingSpace",
                "numberCoInsured"
              ],
              "${'$'}id": "SwedishApartment"
            }
        """.trimIndent()
        val swedishApartmentSchemaJsonNode = objectMapper.readTree(swedishApartmentSchemaJson)
        val swedishApartmentSchema = schemaServiceToTest.getSchemaByContractType(ContractType.SWEDISH_APARTMENT)
        assertThat(swedishApartmentSchema.toString()).isEqualTo(swedishApartmentSchemaJsonNode.toString())
    }

    @Test
    fun `validate house schema`() {
        val swedishHouseSchemaJson = """
            {
              "${'$'}schema": "http://json-schema.org/draft-07/schema#",
              "type": "object",
              "properties": {
                "street": {
                  "type": "string",
                  "title": "Street"
                },
                "zipCode": {
                  "type": "string",
                  "title": "Zip Code",
                  "minLength": 5,
                  "maxLength": 5
                },
                "city": {
                  "type": "string",
                  "title": "City"
                },
                "livingSpace": {
                  "type": "integer",
                  "title": "Living Space",
                  "minimum": 0
                },
                "numberCoInsured": {
                  "type": "integer",
                  "title": "Number Co-Insured",
                  "minimum": 0
                },
                "ancillaryArea": {
                  "type": "integer",
                  "title": "Ancillary Area",
                  "minimum": 0
                },
                "yearOfConstruction": {
                  "type": "integer",
                  "title": "Year Of Construction",
                  "minimum": 0
                },
                "numberOfBathrooms": {
                  "type": "integer",
                  "title": "Number Of Bathrooms",
                  "minimum": 0
                },
                "isSubleted": {
                  "type": "boolean",
                  "title": "Is Subleted",
                  "default": false
                },
                "extraBuildings": {
                  "type": "array",
                  "items": {
                    "type": "object",
                    "properties": {
                      "type": {
                        "type": "string",
                        "enum": [
                          "GARAGE",
                          "CARPORT",
                          "SHED",
                          "STOREHOUSE",
                          "FRIGGEBOD",
                          "ATTEFALL",
                          "OUTHOUSE",
                          "GUESTHOUSE",
                          "GAZEBO",
                          "GREENHOUSE",
                          "SAUNA",
                          "BARN",
                          "BOATHOUSE",
                          "OTHER"
                        ],
                        "title": "Type",
                        "default": "GARAGE"
                      },
                      "area": {
                        "type": "integer",
                        "title": "Area",
                        "minimum": 0
                      },
                      "hasWaterConnected": {
                        "type": "boolean",
                        "title": "Has Water Connected",
                        "default": false
                      }
                    },
                    "required": [
                      "type",
                      "area",
                      "hasWaterConnected"
                    ]
                  },
                  "default": []
                }
              },
              "required": [
                "street",
                "zipCode",
                "livingSpace",
                "numberCoInsured",
                "ancillaryArea",
                "yearOfConstruction",
                "numberOfBathrooms",
                "isSubleted",
                "extraBuildings"
              ],
              "${'$'}id": "SwedishHouse"
            }
        """.trimIndent()
        val swedishHouseSchemaJsonNode = objectMapper.readTree(swedishHouseSchemaJson)
        val swedishHouseSchema = schemaServiceToTest.getSchemaByContractType(ContractType.SWEDISH_HOUSE)
        assertThat(swedishHouseSchema.toString()).isEqualTo(swedishHouseSchemaJsonNode.toString())
    }

    @Test
    fun `validate norwegian home content schema`() {
        val norwegianHomeContentSchemaJson = """
            {
              "${'$'}schema": "http://json-schema.org/draft-07/schema#",
              "type": "object",
              "properties": {
                "lineOfBusiness": {
                  "type": "string",
                  "enum": [
                    "RENT",
                    "OWN"
                  ],
                  "title": "Line Of Business"
                },
                "street": {
                  "type": "string",
                  "title": "Street"
                },
                "zipCode": {
                  "type": "string",
                  "title": "Zip Code",
                  "minLength": 4,
                  "maxLength": 4
                },
                "city": {
                  "type": "string",
                  "title": "City"
                },
                "livingSpace": {
                  "type": "integer",
                  "title": "Living Space",
                  "minimum": 0
                },
                "numberCoInsured": {
                  "type": "integer",
                  "title": "Number Co-Insured",
                  "minimum": 0
                },
                "isYouth": {
                  "type": "boolean",
                  "title": "Is Youth",
                  "default": false
                }
              },
              "required": [
                "lineOfBusiness",
                "street",
                "zipCode",
                "livingSpace",
                "numberCoInsured",
                "isYouth"
              ],
              "${'$'}id": "NorwegianHomeContent"
            }
        """.trimIndent()
        val norwegianHomeContentSchemaJsonNode = objectMapper.readTree(norwegianHomeContentSchemaJson)
        val norwegianHomeContentSchema = schemaServiceToTest.getSchemaByContractType(ContractType.NORWEGIAN_HOME_CONTENT)
        assertThat(norwegianHomeContentSchema.toString()).isEqualTo(norwegianHomeContentSchemaJsonNode.toString())
    }

    @Test
    fun `validate norwegian travel schema`() {
        val norwegianTravelSchemaJson = """
            {
              "${'$'}schema": "http://json-schema.org/draft-07/schema#",
              "type": "object",
              "properties": {
                "numberCoInsured": {
                  "type": "integer",
                  "title": "Number Co-Insured",
                  "minimum": 0
                },
                "isYouth": {
                  "type": "boolean",
                  "title": "Is Youth",
                  "default": false
                }
              },
              "required": [
                "numberCoInsured",
                "isYouth"
              ],
              "${'$'}id": "NorwegianTravel"
            }
        """.trimIndent()
        val norwegianTravelSchemaJsonNode = objectMapper.readTree(norwegianTravelSchemaJson)
        val norwegianTravelSchema = schemaServiceToTest.getSchemaByContractType(ContractType.NORWEGIAN_TRAVEL)
        assertThat(norwegianTravelSchema.toString()).isEqualTo(norwegianTravelSchemaJsonNode.toString())
    }
}
