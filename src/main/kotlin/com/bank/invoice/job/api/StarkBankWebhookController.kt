package com.bank.invoice.job.api

import com.bank.invoice.job.application.TransferService
import com.bank.invoice.job.dto.WebhookEventWrapper
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import tools.jackson.module.kotlin.jacksonObjectMapper

@RestController
@RequestMapping("/api/webhook/invoices")
class StarkBankWebhookController(
    private val transferService: TransferService
) {
    private val logger = LoggerFactory.getLogger(StarkBankWebhookController::class.java)
    private val mapper = jacksonObjectMapper()

    @PostMapping
    fun getEvent(
        @RequestBody body: String,
        @RequestHeader("Digital-Signature") signature: String
    ): ResponseEntity<String> {
        try {
            val webhookDto = mapper.readValue(body, WebhookEventWrapper::class.java)
            val subscription = webhookDto.event.subscription
            val log = webhookDto.event.log
            val invoice = webhookDto.event.log.invoice

            if (subscription != "invoice") {
                logger.warn("Subscription does not match subscription Invoice")
                return ResponseEntity.badRequest().build()
            }

            if (log.errors.isNotEmpty()) {
                logger.info("Event has errors: {}", log.errors[0])
            }

            logger.info("Invoice received: {}", invoice)

            transferService.processPaidInvoice(invoice)

            return ResponseEntity.ok("Received")
        } catch (e: Exception) {
            logger.error(e.message)
            return ResponseEntity.badRequest().build()
        }
    }
}