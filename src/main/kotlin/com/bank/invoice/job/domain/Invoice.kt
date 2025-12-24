package com.bank.invoice.job.domain

import java.time.LocalDateTime

data class Invoice(
    val id: String? = null,
    val amount: Long,
    val taxId: String,
    val name: String,
    val status: InvoiceStatus = InvoiceStatus.CREATED,
    val due: LocalDateTime? = null,
    val tags: Set<String> = emptySet()
) {
    init {
        require(amount > 0) { "Amount must be positive" }
        require(taxId.isNotBlank()) { "Tax ID is mandatory" }
        require(name.isNotBlank()) { "Name is mandatory" }
    }

    companion object {
        fun createRequest(amount: Long, taxId: String, name: String): Invoice {
            return Invoice(
                amount = amount,
                taxId = taxId,
                name = name,
                due = LocalDateTime.now().plusDays(2)
            )
        }
    }
}