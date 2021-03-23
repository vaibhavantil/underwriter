package com.hedvig.underwriter.model

import com.hedvig.libs.logging.masking.Masked

data class Name(
    @Masked val firstName: String,
    @Masked val lastName: String
)
