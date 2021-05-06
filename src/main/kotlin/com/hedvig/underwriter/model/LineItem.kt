package com.hedvig.underwriter.model

import com.fasterxml.jackson.annotation.JsonIgnore
import java.math.BigDecimal

data class LineItem(

    @JsonIgnore
    val internalId: Int? = null,
    @JsonIgnore
    val revisionId: Int? = null,

    val type: String,
    val subType: String,
    val amount: BigDecimal
)
