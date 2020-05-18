package com.hedvig.underwriter.graphql

import com.hedvig.graphql.commons.errors.GraphQlErrorException
import graphql.ErrorType

class QuoteNotFoundQueryException(val memberId: String) : GraphQlErrorException(
    "No quote found for memberId: $memberId",
    ErrorType.ValidationError,
    mapOf("code" to "InputvalidationError")
)
