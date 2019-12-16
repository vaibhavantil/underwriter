package com.hedvig.underwriter.graphql.type

import java.lang.IllegalArgumentException

data class CurrentInsurer(
    val id: String,
    val displayName: String,
    val switchable: Boolean
) {
    companion object {
        fun create(id: String): CurrentInsurer {
            return when (id) {
                "if" -> CurrentInsurer(
                    id,
                    "If",
                    false
                )
                "Folksam" -> CurrentInsurer(
                    id,
                    "Folksam",
                    true
                )
                "Trygg-Hansa" -> CurrentInsurer(
                    id,
                    "Trygg-Hansa",
                    true
                )
                "Länsförsäkringar" -> CurrentInsurer(
                    id,
                    "Länsförsäkringar",
                    false
                )
                "Moderna" -> CurrentInsurer(
                    id,
                    "Moderna",
                    false
                )
                "ICA" -> CurrentInsurer(
                    id,
                    "Ica",
                    true
                )
                "Gjensidige" -> CurrentInsurer(
                    id,
                    "Gjensidige",
                    false
                )
                "Vardia" -> CurrentInsurer(
                    id,
                    "Vardia",
                    false
                )
                "Tre Kronor" -> CurrentInsurer(
                    id,
                    "Tre Kronor",
                    true
                )
                "Dina Försäkringar" -> CurrentInsurer(
                    id,
                    "Dina Försäkringar",
                    false
                )
                else -> throw IllegalArgumentException("Unknown id($id) when creating CurrentInsurer")
            }
        }
    }
}
