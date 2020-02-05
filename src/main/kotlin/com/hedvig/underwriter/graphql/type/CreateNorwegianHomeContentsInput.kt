package com.hedvig.underwriter.graphql.type

data class CreateNorwegianHomeContentsInput(
    val street: String,
    val zipCode: String,
    val coinsured: Int,
    val livingSpace: Int,
    val isStudent: Boolean,
    val type: NorwegianHomeContentsType
)
