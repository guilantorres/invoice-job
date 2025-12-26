package com.bank.invoice.job.application

import com.bank.invoice.job.domain.Invoice
import com.bank.invoice.job.domain.provider.InvoiceProvider
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*

@ExtendWith(MockitoExtension::class)
class InvoiceIssuerServiceTest {

    @Mock
    lateinit var invoiceProvider: InvoiceProvider
    @InjectMocks
    lateinit var invoiceIssuerService: InvoiceIssuerService

    private fun buildFakeReturnInvoice(): Invoice {
        return Invoice(
            id = "test-id",
            amount = 1000,
            taxId = "01234567890",
            name = "Returned Spider Man"
        )
    }

    @Test
    fun `should issue between 8 and 12 invoices successfully`() {
        whenever(invoiceProvider.create(any())).thenReturn(buildFakeReturnInvoice())

        invoiceIssuerService.issueInvoice()

        verify(invoiceProvider, atLeast(8)).create(any())
        verify(invoiceProvider, atMost(12)).create(any())
    }

    @Test
    fun `should generate valid invoice data before sending to provider`() {
        whenever(invoiceProvider.create(any())).thenReturn(buildFakeReturnInvoice())

        invoiceIssuerService.issueInvoice()

        val captor = argumentCaptor<Invoice>()

        verify(invoiceProvider, atLeast(8)).create(captor.capture())

        val capturedInvoices = captor.allValues

        capturedInvoices.forEach { invoice ->
            assertTrue(invoice.amount in 100..10000, "Invalid invoice amount: ${invoice.amount}")
            assertTrue(invoice.taxId == "01234567890", "Invalid taxId: ${invoice.taxId}")
        }
    }

    @Test
    fun `should handle provider failure gracefully without crashing the scheduler`() {
        whenever(invoiceProvider.create(any())).thenThrow(RuntimeException("API Down"))

        assertDoesNotThrow {
            invoiceIssuerService.issueInvoice()
        }

        verify(invoiceProvider, atLeast(1)).create(any())
    }

    @Test
    fun `should continue issuing invoices even if specific iterations fail`() {
        val successInvoice = buildFakeReturnInvoice()

        whenever(invoiceProvider.create(any()))
            .thenReturn(successInvoice, successInvoice, successInvoice, successInvoice, successInvoice)
            .thenThrow(RuntimeException("Could not send invoice"), RuntimeException("Could not send invoice"))
            .thenReturn(successInvoice, successInvoice)

        assertDoesNotThrow {
            invoiceIssuerService.issueInvoice()
        }

        verify(invoiceProvider, atLeast(8)).create(any())
    }
}