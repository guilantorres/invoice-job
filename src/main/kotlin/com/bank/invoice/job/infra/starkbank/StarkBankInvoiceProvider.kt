package com.bank.invoice.job.infra.starkbank

import com.bank.invoice.job.domain.Invoice
import com.bank.invoice.job.domain.InvoiceStatus
import com.bank.invoice.job.domain.provider.InvoiceProvider
import com.bank.invoice.job.extension.toStarkIsoFormat
import com.starkbank.Project
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import com.starkbank.Invoice as SdkInvoice

@Component
@Profile("prod")
class StarkBankInvoiceProvider(
    private val project: Project
): InvoiceProvider {

    private val logger = LoggerFactory.getLogger(StarkBankInvoiceProvider::class.java)

    override fun create(invoice: Invoice): Invoice {
        val data = mapOf(
            "amount" to invoice.amount,
            "taxId" to invoice.taxId,
            "name" to invoice.name,
            "due" to invoice.due?.toStarkIsoFormat()
        )

        val sdkRequest = SdkInvoice(data)

        val createdSdkInvoices = SdkInvoice.create(listOf(sdkRequest), project)
        val createdSdkInvoice = createdSdkInvoices.first()

        return invoice.copy(
            id = createdSdkInvoice.id,
            status = InvoiceStatus.fromCode(createdSdkInvoice.status),
        )
    }
}