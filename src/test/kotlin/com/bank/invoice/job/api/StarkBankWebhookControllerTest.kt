package com.bank.invoice.job.api

import com.bank.invoice.job.application.TransferService
import com.bank.invoice.job.dto.WebhookEventData
import com.bank.invoice.job.dto.WebhookEventWrapper
import com.bank.invoice.job.dto.WebhookInvoice
import com.bank.invoice.job.dto.WebhookLog
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(StarkBankWebhookController::class)
class StarkBankWebhookControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc
    @MockitoBean
    lateinit var transferService: TransferService

    private val mapper = jacksonObjectMapper()

    private fun buildWebhookPayload(subscription: String = "invoice"): String {
        val invoice = WebhookInvoice(
            amount = 1000,
            fee = 10,
            status = "paid",
            id = "inv-123",
            brcode = "x",
            created = "now",
            due = "now",
            expiration = 0,
            fine = 0.0,
            fineAmount = 0,
            interest = 0.0,
            interestAmount = 0,
            discountAmount = 0,
            name = "Test",
            nominalAmount = 100,
            taxId = "x",
            updated = "now"
        )

        val fakeAuth = "fake-token"

        val log = WebhookLog(
            id = "log-123",
            created = "2025-01-01T10:00:00",
            type = "created",
            errors = emptyList(),
            invoice = invoice,
            authentication = fakeAuth
        )

        val event = WebhookEventData(
            log = log,
            subscription = subscription,
            id = "evt-123",
            created = "2025-01-01",
            workspaceId = "workspace-1"
        )

        return mapper.writeValueAsString(WebhookEventWrapper(event = event))
    }

    @Test
    fun `should return 200 OK when valid invoice webhook is received`() {
        val payload = buildWebhookPayload()

        mockMvc.perform(post("/api/webhook/invoices")
            .contentType(MediaType.APPLICATION_JSON)
            .content(payload)
            .header("Digital-Signature", "fake-signature"))
            .andExpect(status().isOk)
            .andExpect(content().string("Received"))

        verify(transferService).processPaidInvoice(any())
    }

    @Test
    fun `should return 500 Internal Server Error when Service fails to force retry`() {
        val payload = buildWebhookPayload()

        doThrow(RuntimeException("Database connection failed"))
            .whenever(transferService).processPaidInvoice(any())

        mockMvc.perform(post("/api/webhook/invoices")
            .contentType(MediaType.APPLICATION_JSON)
            .content(payload)
            .header("Digital-Signature", "fake-signature"))
            .andExpect(status().isInternalServerError)
    }

    @Test
    fun `should return 400 Bad Request when JSON is invalid`() {
        mockMvc.perform(post("/api/webhook/invoices")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{ \"invalid\": \"json\" }")
            .header("Digital-Signature", "fake-signature"))
            .andExpect(status().isBadRequest)
    }
}