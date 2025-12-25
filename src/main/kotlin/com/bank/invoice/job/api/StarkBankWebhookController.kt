package com.bank.invoice.job.api

import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import tools.jackson.module.kotlin.jacksonObjectMapper

@RestController
@RequestMapping("/api/webhook/invoices")
class StarkBankWebhookController() {
    private val logger = LoggerFactory.getLogger(StarkBankWebhookController::class.java)
    private val mapper = jacksonObjectMapper()

    @PostMapping
    fun getEvent(
        @RequestBody body: String,
        @RequestHeader("Digital-Signature") signature: String
    ): ResponseEntity<String> {
        try {
            val jsonBody = mapper.readTree(body)
            val event = jsonBody.get("event")
            val subscription = event.path("subscription")

            if (subscription.stringValue() != "invoice") {
                logger.warn("Subscription does not match subscription Invoice")
                return ResponseEntity.badRequest().build()
            }

            val log = event.path("log")
            val errors = event.path("errors")

            if (errors.isArray && errors.size() > 0) {
                logger.info("Event has errors: {}", errors.values())
            }

            val invoice = log.path("invoice")
            val amount = invoice.path("amount").asLong()

            logger.info("Amount received: {}", amount)

            return ResponseEntity.ok("Received")
        } catch (e: Exception) {
            logger.error(e.message)
            return ResponseEntity.badRequest().build()
        }
    }
}