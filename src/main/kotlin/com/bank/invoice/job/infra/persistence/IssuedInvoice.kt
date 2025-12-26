package com.bank.invoice.job.infra.persistence

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "issued_invoices")
data class IssuedInvoice(
    @Id
    val invoiceId: String,
    val amount: Long,
    val taxId: String,
    val name: String,
    val issuedAt: LocalDateTime = LocalDateTime.now()
)