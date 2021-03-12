package com.hedvig.underwriter.web.dtos

import java.util.UUID
import javax.validation.constraints.NotEmpty

data class QuoteBundleRequestDto(
    @get:NotEmpty
    val quoteIds: List<UUID>
)
