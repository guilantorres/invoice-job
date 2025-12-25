package com.bank.invoice.job.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class WebhookEventWrapper(
    val event: WebhookEventData
)
