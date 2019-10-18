package com.hedvig.underwriter.model

import com.hedvig.underwriter.testhelp.JdbiRule
import java.time.Instant
import java.util.UUID
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test

class QuoteRepositoryImplTest {
    @get:Rule
    val jdbiRule = JdbiRule.create()

    @Test
    fun insertsAndFindsApartmentQuotes() {
        val quoteDao = QuoteRepositoryImpl(jdbiRule.jdbi)

        val quote = Quote(
            createdAt = Instant.now(),
            quotedAt = Instant.now().plusSeconds(1),
            signedAt = Instant.now().plusSeconds(2),
            productType = ProductType.APARTMENT,
            data = ApartmentData(
                firstName = "Sherlock",
                lastName = "Holmes",
                ssn = "199003041234",
                street = "221 Baker street",
                zipCode = "11216",
                livingSpace = 33,
                householdSize = 4,
                city = "London",
                id = UUID.randomUUID(),
                subType = ApartmentProductSubType.BRF
            ),
            initiatedFrom = QuoteInitiatedFrom.APP,
            attributedTo = Partner.HEDVIG,
            id = UUID.randomUUID(),
            currentInsurer = null
        )
        quoteDao.insert(quote)
        assertThat(quoteDao.find(quote.id)).isEqualTo(quote)
    }

    @Test
    fun updatesApartmentQuotes() {
        val quoteDao = QuoteRepositoryImpl(jdbiRule.jdbi)

        val quote = Quote(
            createdAt = Instant.now(),
            quotedAt = Instant.now().plusSeconds(1),
            signedAt = Instant.now().plusSeconds(2),
            productType = ProductType.APARTMENT,
            data = ApartmentData(
                firstName = "Sherlock",
                lastName = "Holmes",
                ssn = "199003041234",
                street = "221 Baker street",
                zipCode = "11216",
                livingSpace = 33,
                householdSize = 4,
                city = "London",
                id = UUID.randomUUID(),
                subType = ApartmentProductSubType.BRF
            ),
            initiatedFrom = QuoteInitiatedFrom.APP,
            attributedTo = Partner.HEDVIG,
            id = UUID.randomUUID(),
            currentInsurer = null
        )
        quoteDao.insert(quote)
        val updatedQuote = quote.copy(
            quotedAt = Instant.now().plusSeconds(4),
            signedAt = Instant.now().plusSeconds(5),
            data = (quote.data as ApartmentData).copy(
                firstName = "John",
                lastName = "Watson"
            )
        )
        quoteDao.update(updatedQuote)

        assertThat(quoteDao.find(quote.id)).isEqualTo(updatedQuote)
    }

    @Test
    fun insertsAndFindsHouseQuotes() {
        val quoteDao = QuoteRepositoryImpl(jdbiRule.jdbi)

        val quote = Quote(
            createdAt = Instant.now(),
            quotedAt = Instant.now().plusSeconds(1),
            signedAt = Instant.now().plusSeconds(2),
            productType = ProductType.APARTMENT,
            data = HouseData(
                firstName = "Sherlock",
                lastName = "Holmes",
                ssn = "199003041234",
                street = "221 Baker street",
                zipCode = "11216",
                livingSpace = 33,
                householdSize = 4,
                city = "London",
                id = UUID.randomUUID()
            ),
            initiatedFrom = QuoteInitiatedFrom.APP,
            attributedTo = Partner.HEDVIG,
            id = UUID.randomUUID(),
            currentInsurer = null
        )
        quoteDao.insert(quote)
        assertThat(quoteDao.find(quote.id)).isEqualTo(quote)
    }

    @Test
    fun updatesHouseQuotes() {
        val quoteDao = QuoteRepositoryImpl(jdbiRule.jdbi)

        val quote = Quote(
            createdAt = Instant.now(),
            quotedAt = Instant.now().plusSeconds(1),
            signedAt = Instant.now().plusSeconds(2),
            productType = ProductType.APARTMENT,
            data = HouseData(
                firstName = "Sherlock",
                lastName = "Holmes",
                ssn = "199003041234",
                street = "221 Baker street",
                zipCode = "11216",
                livingSpace = 33,
                householdSize = 4,
                city = "London",
                id = UUID.randomUUID()
            ),
            initiatedFrom = QuoteInitiatedFrom.APP,
            attributedTo = Partner.HEDVIG,
            id = UUID.randomUUID(),
            currentInsurer = null
        )
        quoteDao.insert(quote)

        val updatedQuote = quote.copy(
            quotedAt = Instant.now().plusSeconds(5),
            signedAt = Instant.now().plusSeconds(6),
            data = (quote.data as HouseData).copy(
                firstName = "John",
                lastName = "Watson"
            )
        )
        quoteDao.update(updatedQuote)

        assertThat(quoteDao.find(quote.id)).isEqualTo(updatedQuote)
    }
}
