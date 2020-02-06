package com.hedvig.underwriter.graphql.type

data class CurrentInsurer(
    val id: String,
    val displayName: String,
    val switchable: Boolean
) {
    companion object {
        val insurerMap = mapOf<String, Pair<String, Boolean>>(
            "if" to Pair("If", false),
            "Folksam" to Pair("Folksam", true),
            "Trygg-Hansa" to Pair("Trygg-Hansa", true),
            "Länsförsäkringar" to Pair("Länsförsäkringar", false),
            "Moderna" to Pair("Moderna", true),
            "Gjensidige" to Pair("Gjensidige", false),
            "Vardia" to Pair("Vardia", false),
            "Tre Kronor" to Pair("Tre Kronor", true),
            "ICA" to Pair("Ica", true),
            "Dina Försäkringar" to Pair("Dina Försäkringar", false),
            "other" to Pair("Other", false)
        )

        fun create(id: String) = insurerMap[id]?.let {
            CurrentInsurer(id, it.first, it.second)
        } ?: throw IllegalArgumentException("Unknown id($id) when creating CurrentInsurer")
    }
}
