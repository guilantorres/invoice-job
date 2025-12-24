package com.bank.invoice.job.infra.mock

import com.bank.invoice.job.domain.Invoice
import com.bank.invoice.job.domain.InvoiceStatus
import com.bank.invoice.job.domain.provider.InvoiceProvider
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.util.UUID

@Component
@Profile("dev", "test", "default")
class MockInvoiceProvider: InvoiceProvider {
    override fun create(invoice: Invoice): Invoice {
        return invoice.copy(
            id = UUID.randomUUID().toString(),
            status = InvoiceStatus.CREATED
        )
    }
}