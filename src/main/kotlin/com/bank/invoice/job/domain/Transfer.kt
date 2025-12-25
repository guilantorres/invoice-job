package com.bank.invoice.job.domain

import java.time.LocalDateTime

data class Transfer(
    val id: String? = null,
    val amount: Long,
    val taxId: String,
    val name: String,
    val bankCode: String,
    val branchCode: String,
    val accountNumber: String,
    val accountType: String,
    val status: String? = null,
    val created: LocalDateTime? = null
)