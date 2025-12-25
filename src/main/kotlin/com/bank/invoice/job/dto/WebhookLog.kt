package com.bank.invoice.job.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class WebhookLog(
    val authentication: String,
    val created: String,
    val errors: List<String>,
    val id: String,
    val invoice: WebhookInvoice,
    val type: String
)
