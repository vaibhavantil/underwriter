package com.hedvig.underwriter.web

import com.hedvig.underwriter.serviceIntegration.memberService.dtos.IsSsnAlreadySignedMemberResponse
import com.hedvig.underwriter.serviceIntegration.memberService.dtos.UnderwriterQuoteSignResponse
import com.hedvig.underwriter.serviceIntegration.notificationService.NotificationServiceClient
import com.hedvig.underwriter.serviceIntegration.priceEngine.dtos.PriceQueryResponse
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.contract.CreateContractResponse
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.javamoney.moneta.Money
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isGreaterThan
import com.hedvig.productPricingObjects.dtos.Agreement
import com.hedvig.productPricingObjects.enums.AgreementStatus
import com.hedvig.underwriter.model.DanishAccidentData
import com.hedvig.underwriter.model.DanishHomeContentsData
import com.hedvig.underwriter.model.DanishTravelData
import com.hedvig.underwriter.model.NorwegianHomeContentsData
import com.hedvig.underwriter.model.NorwegianTravelData
import com.hedvig.underwriter.model.QuoteData
import com.hedvig.underwriter.model.SwedishApartmentData
import com.hedvig.underwriter.model.SwedishHouseData
import com.hedvig.underwriter.serviceIntegration.memberService.MemberServiceClient
import com.hedvig.underwriter.serviceIntegration.memberService.dtos.Flag
import com.hedvig.underwriter.serviceIntegration.memberService.dtos.HelloHedvigResponseDto
import com.hedvig.underwriter.serviceIntegration.memberService.dtos.PersonStatusDto
import com.hedvig.underwriter.serviceIntegration.priceEngine.PriceEngineClient
import com.hedvig.underwriter.serviceIntegration.productPricing.ProductPricingClient
import com.hedvig.underwriter.testhelp.QuoteClient
import io.mockk.mockk
import org.jdbi.v3.core.Jdbi
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.ResponseEntity
import org.springframework.test.context.junit4.SpringRunner
import java.lang.RuntimeException
import java.time.Instant
import java.util.UUID

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GdprIntegrationTest {

    @Autowired
    private lateinit var quoteClient: QuoteClient

    @Autowired
    private lateinit var jdbi: Jdbi

    @MockkBean(relaxed = true)
    lateinit var notificationServiceClient: NotificationServiceClient

    @MockkBean
    lateinit var priceEngineClient: PriceEngineClient

    @MockkBean
    lateinit var memberServiceClient: MemberServiceClient

    @MockkBean
    lateinit var productPricingClient: ProductPricingClient

    val activeAgreement = Agreement.SwedishApartment(UUID.randomUUID(), mockk(), mockk(), mockk(), null, AgreementStatus.ACTIVE, mockk(), mockk(), 0, 100)

    @Before
    fun setup() {
        every { memberServiceClient.personStatus(any()) } returns ResponseEntity.status(200).body(PersonStatusDto(Flag.GREEN))
        every { memberServiceClient.checkPersonDebt(any()) } returns ResponseEntity.status(200).body(null)
        every { memberServiceClient.checkIsSsnAlreadySignedMemberEntity(any()) } returns IsSsnAlreadySignedMemberResponse(false)
        every { memberServiceClient.createMember() } returns ResponseEntity.status(200).body(HelloHedvigResponseDto("12345"))
        every { memberServiceClient.updateMemberSsn(any(), any()) } returns Unit
        every { memberServiceClient.signQuote(any(), any()) } returns ResponseEntity.status(200).body(UnderwriterQuoteSignResponse(1L, true))
        every { memberServiceClient.finalizeOnBoarding(any(), any()) } returns ResponseEntity.status(200).body("")
        every { productPricingClient.createContract(any(), any()) } returns listOf(CreateContractResponse(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()))
        every { productPricingClient.getAgreement(any()) } returns ResponseEntity.status(200).body(activeAgreement)
        every { priceEngineClient.queryPrice(any()) } returns PriceQueryResponse(UUID.randomUUID(), Money.of(12, "NOK"))
    }

    @Test
    fun `Test deleting quotes`() {

        // Added this snippet to make sure we do not forget to add tests for new types when added
        val makeSureAllTypesAreTested: (QuoteData) -> Unit = fun (type: QuoteData) {
            when (type) {
                is SwedishApartmentData -> TODO()
                is SwedishHouseData -> TODO()
                is NorwegianHomeContentsData -> TODO()
                is NorwegianTravelData -> TODO()
                is DanishAccidentData -> TODO()
                is DanishHomeContentsData -> TODO()
                is DanishTravelData -> TODO()
            }
        }

        val seApartmentRsp = quoteClient.createSwedishApartmentQuote()
        val seHouseRsp = quoteClient.createSwedishHouseQuote()
        val noHomeRsp = quoteClient.createNorwegianHomeContentQuote()
        val noTravelRsp = quoteClient.createNorwegianTravelQuote()
        val dkHomeRsp = quoteClient.createDanishHomeContentQuote()
        val dkAccidentRsp = quoteClient.createDanishAccidentQuote()
        val dkTravelRsp = quoteClient.createDanishTravelQuote()

        assertDeleteQuote(seApartmentRsp.id, "SwedishApartmentData")
        assertDeleteQuote(seHouseRsp.id, "SwedishHouseData")
        assertDeleteQuote(noHomeRsp.id, "NorwegianHomeContentsData")
        assertDeleteQuote(noTravelRsp.id, "NorwegianTravelData")
        assertDeleteQuote(dkHomeRsp.id, "DanishHomeContentsData")
        assertDeleteQuote(dkAccidentRsp.id, "DanishAccidentData")
        assertDeleteQuote(dkTravelRsp.id, "DanishTravelData")
    }

    private fun assertDeleteQuote(quoteId: UUID, expType: String) {
        val revs = getQuoteRevsFromDb(quoteId)

        assertThat(revs.size).isGreaterThan(0)
        assertThat(revs.all { it.seApartmentId != null })

        // Delete it
        val delResponse1 = quoteClient.deleteQuote(quoteId)
        assertThat(delResponse1.statusCodeValue).isEqualTo(204)

        // Validate all gone
        assertNoQuoteInDb(quoteId, revs)

        // Validate we have an anonymised copy
        val deleted = getDeletedQuoteFromDb(quoteId)
        assertThat(deleted.type).isEqualTo(expType)

        // Delete it again
        val delResponse2 = quoteClient.deleteQuote(quoteId)
        assertThat(delResponse2.statusCodeValue).isEqualTo(404)
    }

    @Test
    fun `Test deleting quote with agreement fails`() {

        with(quoteClient.createSwedishApartmentQuote(
            ssn = "199110112399",
            street = "ApStreet 1234",
            zip = "1234",
            city = "ApCity"
        )) {
            // Sign it
            quoteClient.signQuote(id, "Apan", "Apansson", "apan@apansson.se")

            val revs = getQuoteRevsFromDb(id)

            assertThat(revs.size).isGreaterThan(0)
            assertThat(revs.all { it.seApartmentId != null })

            val response = quoteClient.deleteQuote(id)

            assertThat(response.statusCodeValue).isEqualTo(403)
        }
    }

    private fun getQuoteRevsFromDb(quoteId: UUID): List<QuoteRev> {
        return jdbi.withHandle<List<QuoteRev>, RuntimeException> { handle ->

            val sql = """
                SELECT 
                r.master_quote_id as quoteId,
                se_a.internal_id as seApartmentId,
                se_h.internal_id as seHomeId,
                no_h.internal_id as noHomeId,
                no_t.internal_id as noTravelId,
                dk_h.internal_id as dkHomeId,
                dk_a.internal_id as dkAccidentId,
                dk_t.internal_id as dkTravelId
                FROM quote_revisions r
                LEFT JOIN quote_revision_apartment_data se_a ON r.quote_apartment_data_id = se_a.internal_id
                LEFT JOIN quote_revision_house_data se_h ON r.quote_house_data_id = se_h.internal_id
                LEFT JOIN quote_revision_norwegian_home_contents_data no_h ON r.quote_norwegian_home_contents_data_id = no_h.internal_id
                LEFT JOIN quote_revision_norwegian_travel_data no_t ON r.quote_norwegian_travel_data_id = no_t.internal_id
                LEFT JOIN quote_revision_danish_home_contents_data dk_h ON r.quote_danish_home_contents_data_id = dk_h.internal_id
                LEFT JOIN quote_revision_danish_accident_data dk_a ON r.quote_danish_accident_data_id = dk_a.internal_id
                LEFT JOIN quote_revision_danish_travel_data dk_t ON r.quote_danish_travel_data_id = dk_t.internal_id
                WHERE r.master_quote_id = :quoteId
            """.trimIndent()

            handle.createQuery(sql)
                .bind("quoteId", quoteId)
                .mapTo(QuoteRev::class.java)
                .list()
        }
    }

    data class QuoteRev(
        val quoteId: UUID,
        val seApartmentId: Int?,
        val seHouseId: Int?,
        val noHomeId: Int?,
        val noTravelId: Int?,
        val dkHomeId: Int?,
        val dkAccidentId: Int?,
        val dkTravelId: Int?
    )

    private fun assertNoQuoteInDb(quoteId: UUID, quoteRevs: List<QuoteRev>) {
        jdbi.withHandle<Unit, RuntimeException> { handle ->

            assertThat(
                handle.createQuery("SELECT count(*) FROM master_quotes WHERE id = :quoteId")
                    .bind("quoteId", quoteId)
                    .mapTo(Int::class.java)
                    .findOnly()
            ).isEqualTo(0)

            assertThat(
                handle.createQuery("SELECT count(*) FROM quote_revisions WHERE master_quote_id = :quoteId")
                    .bind("quoteId", quoteId)
                    .mapTo(Int::class.java)
                    .findOnly()
            ).isEqualTo(0)

            quoteRevs.forEach {
                it.seApartmentId?.let {
                    assertThat(
                        handle.createQuery("SELECT count(*) FROM quote_revision_apartment_data WHERE internal_id = :internalId")
                            .bind("internalId", it)
                            .mapTo(Int::class.java)
                            .findOnly()
                    ).isEqualTo(0)
                }
                it.seHouseId?.let {
                    assertThat(
                        handle.createQuery("SELECT count(*) FROM quote_revision_house_data WHERE internal_id = :internalId")
                            .bind("internalId", it)
                            .mapTo(Int::class.java)
                            .findOnly()
                    ).isEqualTo(0)
                }
                it.noHomeId?.let {
                    assertThat(
                        handle.createQuery("SELECT count(*) FROM quote_revision_norwegian_home_contents_data WHERE internal_id = :internalId")
                            .bind("internalId", it)
                            .mapTo(Int::class.java)
                            .findOnly()
                    ).isEqualTo(0)
                }
                it.noTravelId?.let {
                    assertThat(
                        handle.createQuery("SELECT count(*) FROM quote_revision_norwegian_travel_data WHERE internal_id = :internalId")
                            .bind("internalId", it)
                            .mapTo(Int::class.java)
                            .findOnly()
                    ).isEqualTo(0)
                }
                it.dkHomeId?.let {
                    assertThat(
                        handle.createQuery("SELECT count(*) FROM quote_revision_danish_home_contents_data WHERE internal_id = :internalId")
                            .bind("internalId", it)
                            .mapTo(Int::class.java)
                            .findOnly()
                    ).isEqualTo(0)
                }
                it.dkAccidentId?.let {
                    assertThat(
                        handle.createQuery("SELECT count(*) FROM quote_revision_danish_accident_data WHERE internal_id = :internalId")
                            .bind("internalId", it)
                            .mapTo(Int::class.java)
                            .findOnly()
                    ).isEqualTo(0)
                }
                it.dkTravelId?.let {
                    assertThat(
                        handle.createQuery("SELECT count(*) FROM quote_revision_danish_travel_data WHERE internal_id = :internalId")
                            .bind("internalId", it)
                            .mapTo(Int::class.java)
                            .findOnly()
                    ).isEqualTo(0)
                }
            }
        }
    }

    data class DeletedQuote(
        val quoteId: UUID,
        val createdAt: Instant,
        val deletedAt: Instant,
        val type: String,
        val memberId: String?,
        val quote: String,
        val revs: String
    )

    private fun getDeletedQuoteFromDb(quoteId: UUID): DeletedQuote =
        jdbi.withHandle<DeletedQuote, RuntimeException> { handle ->
            handle.createQuery("SELECT * FROM deleted_quotes WHERE quote_id = :quoteId")
                .bind("quoteId", quoteId)
                .mapTo(DeletedQuote::class.java)
                .findOnly()
        }
}
