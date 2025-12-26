package com.bank.invoice.job.application

import com.bank.invoice.job.domain.Transfer
import com.bank.invoice.job.domain.provider.TransferProvider
import com.bank.invoice.job.dto.WebhookInvoice
import com.bank.invoice.job.infra.persistence.ProcessedEventRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class TransferServiceTest {

    @Mock
    lateinit var transferProvider: TransferProvider
    @Mock
    lateinit var repository: ProcessedEventRepository
    @InjectMocks
    lateinit var transferService: TransferService

    private fun buildInvoice(amount: Long, fee: Long, status: String = "paid"): WebhookInvoice {
        return WebhookInvoice(
            amount = amount,
            fee = fee,
            status = status,
            id = "inv-123",
            brcode = "xyz",
            created = "2025-12-25",
            due = "2025-12-26",
            expiration = 3600,
            fine = 0.0,
            fineAmount = 0,
            interest = 0.0,
            interestAmount = 0,
            discountAmount = 0,
            name = "John Doe",
            nominalAmount = 1000,
            taxId = "123",
            updated = "now"
        )
    }

    @Test
    fun `should create transfer successfully when invoice is paid and amount covers fee`() {
        val amount = 1000L
        val fee = 100L
        val invoice = buildInvoice(amount,fee)

        whenever(repository.existsById(invoice.id)).thenReturn(false)

        val expectedTransfer = Transfer(
            id = "x",
            amount = 900,
            taxId = "x",
            name = "x",
            bankCode = "x",
            branchCode = "x",
            accountNumber = "x",
            accountType = "payment",
            status = "created",
            created = LocalDateTime.now()
        )
        whenever(transferProvider.create(any())).thenReturn(expectedTransfer)

        transferService.processPaidInvoice(invoice)

        val captor = argumentCaptor<Transfer>()
        verify(transferProvider).create(captor.capture())

        val capturedTransfer = captor.firstValue

        assertEquals(900L, capturedTransfer.amount)
        assertEquals("20.018.183/0001-80", capturedTransfer.taxId)
        assertEquals("Stark Bank S.A.", capturedTransfer.name)
        assertEquals("20018183", capturedTransfer.bankCode)
        assertEquals("0001", capturedTransfer.branchCode)
        assertEquals("6341320293482496", capturedTransfer.accountNumber)
        assertEquals("payment", capturedTransfer.accountType)
        assertNotNull(capturedTransfer.created)
        verify(repository).save(any())
    }

    @Test
    fun `should not create transfer when invoice status is not paid`() {
        val invoice = buildInvoice(amount = 1000, fee = 10, status = "created")

        transferService.processPaidInvoice(invoice)

        verify(transferProvider, never()).create(any())
    }

    @Test
    fun `should not create transfer when amount is less than or equal to fee`() {
        val amount = 100L
        val fee = 100L
        val invoice = buildInvoice(amount, fee)

        transferService.processPaidInvoice(invoice)

        verify(transferProvider, never()).create(any())
    }

    @Test
    fun `should throw exception when provider fails to force webhook retry`() {
        val invoice = buildInvoice(amount = 1000, fee = 10)

        whenever(transferProvider.create(any())).thenThrow(RuntimeException("Stark API Timeout"))

        assertThrows<RuntimeException> {
            transferService.processPaidInvoice(invoice)
        }

        verify(transferProvider).create(any())
    }

    @Test
    fun `should ignore duplicate event if already processed`() {
        val invoice = buildInvoice(amount = 1000, fee = 10)

        whenever(repository.existsById(invoice.id)).thenReturn(true)

        transferService.processPaidInvoice(invoice)

        verify(transferProvider, never()).create(any())
        verify(repository, never()).save(any())
    }

}