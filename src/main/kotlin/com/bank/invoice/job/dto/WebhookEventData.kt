package com.bank.invoice.job.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class WebhookEventData(
    val created: String,
    val id: String,
    val log: WebhookLog,
    val subscription: String,
    val workspaceId: String
)
