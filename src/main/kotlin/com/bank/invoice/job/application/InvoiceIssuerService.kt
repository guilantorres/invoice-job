package com.bank.invoice.job.application

import com.bank.invoice.job.domain.Invoice
import com.bank.invoice.job.domain.provider.InvoiceProvider
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import kotlin.random.Random

@Service
class InvoiceIssuerService(
    private val invoiceProvider: InvoiceProvider
) {

    private val logger = LoggerFactory.getLogger(InvoiceIssuerService::class.java)

    fun issueInvoice() {
        try {
            val newInvoice = generateRandomInvoice()
            val issuedInvoice = invoiceProvider.create(newInvoice)
            logger.info("New invoice issued: ${issuedInvoice.id}")
        } catch (e: Exception) {
            logger.error("Error on issuing new invoice: ${e.message}")
        }
    }

    // The following function is for testing only
    private fun generateRandomInvoice(): Invoice {
        val amount = Random.nextLong(100, 10000)
        val taxId = "01234567890"
        val name = "Spider Man ${Random.nextInt(100000)}"
        return Invoice.createRequest(amount, taxId, name)
    }
}