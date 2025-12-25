package com.bank.invoice.job.api

import com.starkbank.Event
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/webhook/invoices")
class StarkBankWebhookController() {
    private val logger = LoggerFactory.getLogger(StarkBankWebhookController::class.java)

    @PostMapping
    fun getEvent(
        @RequestBody body: String,
        @RequestHeader("Digital-Signature") signature: String
    ): ResponseEntity<String> {
        try {
            logger.info("Received body: $body")
            val event = Event.parse(body, signature)
            // logger.info("Received event: $event")
            return ResponseEntity.ok("Received")
        } catch (e: Exception) {
            logger.error(e.message)
            return ResponseEntity.badRequest().build()
        }
    }
}