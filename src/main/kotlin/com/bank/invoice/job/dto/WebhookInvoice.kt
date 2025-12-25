package com.bank.invoice.job.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class WebhookInvoice(
    val amount: Long,
    val brcode: String,
    val created: String,
    val due: String,
    val expiration: Int,
    val fee: Int,
    val fine: Double,
    val fineAmount: Long,
    val id: String,
    val interest: Double,
    val interestAmount: Long,
    val name: String,
    val nominalAmount: Long,
    val status: String,
    val taxId: String,
    val updated: String
)
