package com.bank.invoice.job.domain.provider

import com.bank.invoice.job.domain.Invoice

interface InvoiceProvider {
    fun create(invoice: Invoice): Invoice
}