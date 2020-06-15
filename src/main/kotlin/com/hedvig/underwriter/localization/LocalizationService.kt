package com.hedvig.underwriter.localization

import java.util.Locale

interface LocalizationService {
    fun getTranslation(key: String, locale: Locale): String?
}
