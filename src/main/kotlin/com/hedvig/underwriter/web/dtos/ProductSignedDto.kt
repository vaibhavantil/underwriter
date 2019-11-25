package com.hedvig.underwriter.web.dtos

import java.util.UUID

data class ProductSignedDto(
    val memberId: String,
    val productId: UUID
)
