package com.hedvig.underwriter.web

import com.hedvig.underwriter.service.QuoteService
import com.hedvig.underwriter.testhelp.databuilder.a
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.UUID

@ExtendWith(SpringExtension::class)
@WebMvcTest(controllers = [V2QuoteController::class])
class QuoteSchemaTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    lateinit var quoteService: QuoteService

    /**
     *
     * Get schema quote
     * - SwedishHouse
     * - SwedishApartment
     * - NorwegianTravel
     * - NorwegianHomeContent
     *
     * Quote does not exits
     *
     */

    @Test
    internal fun swedishQuote() {

        every {
            quoteService.getQuote(any())
        } returns a.QuoteBuilder().w(a.SwedishHouseDataBuilder()).build()

        val response = mockMvc.perform(
            MockMvcRequestBuilders.get("/_/v2/quotes/{quoteId}/schema", UUID.randomUUID())
        )

        response
            .andExpect(status().is2xxSuccessful)
            .andExpect(
                MockMvcResultMatchers.content().json(
                    """
            {
                "${'$'}schema":"http://json-schema.org/draft-07/schema#",
                "title":"Swedish House",
                "type":"object",
                "additionalProperties":false,
                "properties":{
                    "street":{
                        "propertyOrder":1,
                        "type":"string",
                        "title":"Street"
                    },
                    "zipCode":{
                        "propertyOrder":2,
                        "type":"string",
                        "title":"Zip Code"
                    },
                    "city":{
                        "propertyOrder":3,
                        "type":"string",
                        "title":"City"
                    },
                    "livingSpace":{
                        "propertyOrder":4,
                        "type":"integer",
                        "title":"Living Space"
                    },
                    "householdSize":{
                        "propertyOrder":5,
                        "type":"integer",
                        "title":"Household Size"
                    },
                    "ancillaryArea":{
                        "propertyOrder":6,
                        "type":"integer",
                        "title":"Ancillary Area"},
                    "yearOfConstruction":{
                        "propertyOrder":7,
                        "type":"integer",
                        "title":"Year Of Construction"
                    },
                    "numberOfBathrooms":{
                        "propertyOrder":8,
                        "type":"integer",
                        "title":"Number Of Bathrooms"
                    },
                    "extraBuildings":{
                        "propertyOrder":9,
                        "type":"array",
                        "format":"table",
                        "items":{
                            "${'$'}ref":"#/definitions/ExtraBuildingRequestDto"
                        },
                        "title":"Extra Buildings"
                    },
                    "subleted":{
                        "propertyOrder":10,
                        "type":"boolean",
                        "title":"Subleted"
                    },
                    "floor":{
                        "propertyOrder":11,
                        "type":"integer",
                        "title":"Floor"
                    }
                },
                "definitions":{
                    "ExtraBuildingRequestDto":{
                        "type":"object",
                        "additionalProperties":false,
                        "properties":{
                            "id":{
                                "propertyOrder":1,
                                "type":"string",
                                "title":"Id"
                            },
                            "type":{
                                "propertyOrder":2,
                                "type":"string",
                                "enum":[
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
                            "title":"Type"
                            },
                            "area":{
                                "propertyOrder":3,
                                "type":"integer",
                                "title":"Area"
                            },
                            "hasWaterConnected":{
                                "propertyOrder":4,
                                "type":"boolean",
                                "title":"Has Water Connected"
                            }
                        },
                        "required":[
                            "type",
                            "area",
                            "hasWaterConnected"
                        ]
                    }
                }
            }
            """.trimIndent()
                )
            )
    }

    @Test
    internal fun swedishApartmentQuote() {

        every {
            quoteService.getQuote(any())
        } returns a.QuoteBuilder().w(a.SwedishApartmentDataBuilder()).build()

        val response = mockMvc.perform(
            MockMvcRequestBuilders.get("/_/v2/quotes/{quoteId}/schema", UUID.randomUUID())
        )

        response
            .andExpect(status().is2xxSuccessful)
            .andExpect(
                MockMvcResultMatchers.content().json(
                    """
            {
              "${'$'}schema": "http://json-schema.org/draft-07/schema#",
              "title": "Swedish Apartment",
              "type": "object",
              "additionalProperties": false,
              "properties": {
                "street": {
                  "propertyOrder": 1,
                  "type": "string",
                  "title": "Street"
                },
                "zipCode": {
                  "propertyOrder": 2,
                  "type": "string",
                  "title": "Zip Code"
                },
                "city": {
                  "propertyOrder": 3,
                  "type": "string",
                  "title": "City"
                },
                "livingSpace": {
                  "propertyOrder": 4,
                  "type": "integer",
                  "title": "Living Space"
                },
                "householdSize": {
                  "propertyOrder": 5,
                  "type": "integer",
                  "title": "Household Size"
                },
                "floor": {
                  "propertyOrder": 6,
                  "type": "integer",
                  "title": "Floor"
                },
                "subType": {
                  "propertyOrder": 7,
                  "type": "string",
                  "enum": [
                    "BRF",
                    "RENT",
                    "STUDENT_BRF",
                    "STUDENT_RENT"
                  ],
                  "title": "Sub Type"
                }
              }
            }
            """.trimIndent()
                )
            )
    }

    @Test
    internal fun norwegianHomeContentQuote() {

        every {
            quoteService.getQuote(any())
        } returns a.QuoteBuilder().w(a.NorwegianHomeContentDataBuilder()).build()

        val response = mockMvc.perform(
            MockMvcRequestBuilders.get("/_/v2/quotes/{quoteId}/schema", UUID.randomUUID())
        )

        response
            .andExpect(status().is2xxSuccessful)
            .andExpect(
                MockMvcResultMatchers.content().json(
                    """
            {
                "${'$'}schema": "http://json-schema.org/draft-07/schema#",
                "title": "Norwegian Home Contents",
                "type": "object",
                "additionalProperties": false,
                "properties": {
                    "street": {
                        "propertyOrder": 1,
                        "type": "string",
                        "title": "Street"
                    },
                    "zipCode": {
                        "propertyOrder": 2,
                        "type": "string",
                        "title": "Zip Code"
                    },
                    "city": {
                        "propertyOrder": 3,
                        "type": "string",
                        "title": "City"
                    },
                    "coInsured": {
                        "propertyOrder": 4,
                        "type": "integer",
                        "title": "Co Insured"
                    },
                    "livingSpace": {
                        "propertyOrder": 5,
                        "type": "integer",
                        "title": "Living Space"
                    },
                    "youth": {
                        "propertyOrder": 6,
                        "type": "boolean",
                        "title": "Youth"
                    },
                    "subType": {
                        "propertyOrder": 7,
                        "type": "string",
                        "enum": [
                            "RENT",
                            "OWN"
                        ],
                        "title": "Sub Type"
                    }
                }
            }
            """.trimIndent()
                )
            )
    }

    @Test
    internal fun norwegianTravelQuote() {

        every {
            quoteService.getQuote(any())
        } returns a.QuoteBuilder().w(a.NorwegianTravelDataBuilder()).build()

        val response = mockMvc.perform(
            MockMvcRequestBuilders.get("/_/v2/quotes/{quoteId}/schema", UUID.randomUUID())
        )

        response
            .andExpect(status().is2xxSuccessful)
            .andExpect(
                MockMvcResultMatchers.content().json(
                    """
            {
                "${'$'}schema": "http://json-schema.org/draft-07/schema#",
                "title": "Norwegian Travel",
                "type": "object",
                "additionalProperties": false,
                "properties": {
                    "coInsured": {
                        "propertyOrder": 1,
                        "type": "integer",
                        "title": "Co Insured"
                    },
                    "youth": {
                        "propertyOrder": 2,
                        "type": "boolean",
                        "title": "Youth"
                    }
                }
            }
            """.trimIndent()
                )
            )
    }
}
