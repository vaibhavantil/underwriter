package com.hedvig.underwriter.repository

import com.hedvig.underwriter.model.IncompleteQuote
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
public interface IncompleteQuoteRepository: CrudRepository<IncompleteQuote, UUID> {

}