package com.hedvig.underwriter.service

import com.fasterxml.jackson.databind.JsonNode
import com.hedvig.underwriter.model.ContractType
import com.hedvig.underwriter.service.model.QuoteSchema
import java.util.UUID

interface QuoteSchemaService {
    fun getSchemaByQuoteId(quoteId: UUID): JsonNode?
    fun getSchemaByContractType(contractType: ContractType): JsonNode
    fun getSchemaDataByQuoteId(quoteId: UUID): QuoteSchema?
}
