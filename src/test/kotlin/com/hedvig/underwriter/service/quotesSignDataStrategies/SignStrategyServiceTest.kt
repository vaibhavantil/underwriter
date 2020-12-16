package com.hedvig.underwriter.service.quotesSignDataStrategies

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import com.hedvig.underwriter.service.model.StartSignErrors
import com.hedvig.underwriter.service.model.StartSignResponse
import com.hedvig.underwriter.service.quotesSignDataStrategies.StrategyHelper.createSignData
import com.hedvig.underwriter.testhelp.databuilder.DanishAccidentDataBuilder
import com.hedvig.underwriter.testhelp.databuilder.DanishHomeContentsDataBuilder
import com.hedvig.underwriter.testhelp.databuilder.DanishTravelDataBuilder
import com.hedvig.underwriter.testhelp.databuilder.NorwegianHomeContentDataBuilder
import com.hedvig.underwriter.testhelp.databuilder.NorwegianTravelDataBuilder
import com.hedvig.underwriter.testhelp.databuilder.SwedishHouseDataBuilder
import com.hedvig.underwriter.testhelp.databuilder.quote
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.core.env.Environment

class SignStrategyServiceTest {

    private val swedishBankIdSignStrategy: SwedishBankIdSignStrategy = mockk(relaxed = true)
    private val redirectSignStrategy: RedirectSignStrategy = mockk(relaxed = true)
    private val simpleSignStrategy: SimpleSignStrategy = mockk(relaxed = true)
    private val env: Environment = mockk(relaxed = true)

    private val cut = SignStrategyService(
        swedishBankIdSignStrategy,
        redirectSignStrategy,
        simpleSignStrategy,
        env
    )

    @Test
    fun `start sign with no quotes returns no quotes`() {
        val result = cut.startSign(emptyList(), createSignData())

        assertThat(result).isInstanceOf(StartSignResponse.FailedToStartSign::class)
        require(result is StartSignResponse.FailedToStartSign)
        assertThat(result.errorMessage).isEqualTo(StartSignErrors.noQuotes.errorMessage)
        assertThat(result.errorCode).isEqualTo(StartSignErrors.noQuotes.errorCode)
    }

    @Test
    fun `start sign swedish and norwegian quote returns can not be bundled`() {
        val result = cut.startSign(
            listOf(
                quote {},
                quote {
                    data = NorwegianTravelDataBuilder()
                }
            ),
            createSignData()
        )

        assertThat(result).isInstanceOf(StartSignResponse.FailedToStartSign::class)
        require(result is StartSignResponse.FailedToStartSign)
        assertThat(result.errorMessage).isEqualTo(StartSignErrors.quotesCanNotBeBundled.errorMessage)
        assertThat(result.errorCode).isEqualTo(StartSignErrors.quotesCanNotBeBundled.errorCode)
    }

    @Test
    fun `start sign norwegian and danish quote returns can not be bundled`() {
        val result = cut.startSign(
            listOf(
                quote {
                    data = NorwegianTravelDataBuilder()
                },
                quote {
                    data = DanishHomeContentsDataBuilder()
                }
            ),
            createSignData()
        )

        assertThat(result).isInstanceOf(StartSignResponse.FailedToStartSign::class)
        require(result is StartSignResponse.FailedToStartSign)
        assertThat(result.errorMessage).isEqualTo(StartSignErrors.quotesCanNotBeBundled.errorMessage)
        assertThat(result.errorCode).isEqualTo(StartSignErrors.quotesCanNotBeBundled.errorCode)
    }

    @Test
    fun `start sign with two swedish quotes returns can not be bundled`() {
        val result = cut.startSign(
            listOf(
                quote {},
                quote {
                    data = SwedishHouseDataBuilder()
                }
            ),
            createSignData()
        )

        assertThat(result).isInstanceOf(StartSignResponse.FailedToStartSign::class)
        require(result is StartSignResponse.FailedToStartSign)
        assertThat(result.errorMessage).isEqualTo(StartSignErrors.quotesCanNotBeBundled.errorMessage)
        assertThat(result.errorCode).isEqualTo(StartSignErrors.quotesCanNotBeBundled.errorCode)
    }

    @Test
    fun `start sign with two norwegian home content quotes returns can not be bundled`() {
        val result = cut.startSign(
            listOf(
                quote {
                    data = NorwegianHomeContentDataBuilder()
                },
                quote {
                    data = NorwegianHomeContentDataBuilder()
                }
            ),
            createSignData()
        )

        assertThat(result).isInstanceOf(StartSignResponse.FailedToStartSign::class)
        require(result is StartSignResponse.FailedToStartSign)
        assertThat(result.errorMessage).isEqualTo(StartSignErrors.quotesCanNotBeBundled.errorMessage)
        assertThat(result.errorCode).isEqualTo(StartSignErrors.quotesCanNotBeBundled.errorCode)
    }

    @Test
    fun `start sign with two norwegian travel quotes returns can not be bundled`() {
        val result = cut.startSign(
            listOf(
                quote {
                    data = NorwegianTravelDataBuilder()
                },
                quote {
                    data = NorwegianTravelDataBuilder()
                }
            ),
            createSignData()
        )

        assertThat(result).isInstanceOf(StartSignResponse.FailedToStartSign::class)
        require(result is StartSignResponse.FailedToStartSign)
        assertThat(result.errorMessage).isEqualTo(StartSignErrors.quotesCanNotBeBundled.errorMessage)
        assertThat(result.errorCode).isEqualTo(StartSignErrors.quotesCanNotBeBundled.errorCode)
    }

    @Test
    fun `start sign with one danish accident quotes returns can not be bundled`() {
        val result = cut.startSign(
            listOf(
                quote {
                    data = DanishAccidentDataBuilder()
                }
            ),
            createSignData()
        )

        assertThat(result).isInstanceOf(StartSignResponse.FailedToStartSign::class)
        require(result is StartSignResponse.FailedToStartSign)
        assertThat(result.errorMessage).isEqualTo(StartSignErrors.singleQuoteCanNotBeSignedAlone.errorMessage)
        assertThat(result.errorCode).isEqualTo(StartSignErrors.singleQuoteCanNotBeSignedAlone.errorCode)
    }

    @Test
    fun `start sign with one danish travel quotes returns can not be bundled`() {
        val result = cut.startSign(
            listOf(
                quote {
                    data = DanishTravelDataBuilder()
                }
            ),
            createSignData()
        )

        assertThat(result).isInstanceOf(StartSignResponse.FailedToStartSign::class)
        require(result is StartSignResponse.FailedToStartSign)
        assertThat(result.errorMessage).isEqualTo(StartSignErrors.singleQuoteCanNotBeSignedAlone.errorMessage)
        assertThat(result.errorCode).isEqualTo(StartSignErrors.singleQuoteCanNotBeSignedAlone.errorCode)
    }

    @Test
    fun `start sign of one swedish quote calls swedishBankIdSignStrategy startSign`() {
        cut.startSign(
            listOf(
                quote {}
            ),
            createSignData()
        )

        verify(exactly = 1) { swedishBankIdSignStrategy.startSign(any(), any()) }
    }

    @Test
    fun `start sign of norwegian quotes calls redirectSignStrategy startSign`() {
        cut.startSign(
            listOf(
                quote {
                    data = NorwegianHomeContentDataBuilder()
                },
                quote {
                    data = NorwegianTravelDataBuilder()
                }
            ),
            createSignData()
        )

        verify(exactly = 1) { redirectSignStrategy.startSign(any(), any()) }
    }

    @Test
    fun `start sign of norwegian quotes with enableSimpleSign set and activeProfiles profile staging has   to true calls simpleSignStrategy startSign`() {
        every {
            env.activeProfiles
        } returns arrayOf("staging")

        cut.startSign(
            listOf(
                quote {
                    data = NorwegianHomeContentDataBuilder()
                },
                quote {
                    data = NorwegianTravelDataBuilder()
                }
            ),
            createSignData(enableSimpleSign = true)
        )

        verify(exactly = 1) { simpleSignStrategy.startSign(any(), any()) }
    }

    @Test
    fun `start sign of danish quotes calls redirectSignStrategy startSign`() {
        cut.startSign(
            listOf(
                quote {
                    data = DanishAccidentDataBuilder()
                },
                quote {
                    data = DanishTravelDataBuilder()
                },
                quote {
                    data = DanishHomeContentsDataBuilder()
                }
            ),
            createSignData()
        )

        verify(exactly = 1) { redirectSignStrategy.startSign(any(), any()) }
    }
}
