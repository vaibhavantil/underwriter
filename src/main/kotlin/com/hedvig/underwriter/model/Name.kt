package com.hedvig.underwriter.model

import com.hedvig.underwriter.util.logging.Masked

data class Name(
    @Masked val firstName: String,
    @Masked val lastName: String
)
