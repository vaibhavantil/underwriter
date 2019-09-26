package com.hedvig.underwriter.repository

import com.hedvig.underwriter.model.CompleteQuote
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
public interface CompleteQuoteRepository: CrudRepository<CompleteQuote, UUID> {

}