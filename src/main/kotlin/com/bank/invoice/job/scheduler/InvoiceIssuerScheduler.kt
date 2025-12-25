package com.bank.invoice.job.scheduler

import com.bank.invoice.job.application.InvoiceIssuerService
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class InvoiceIssuerScheduler(
    private val invoiceIssuerService: InvoiceIssuerService
) {

    private val logger = LoggerFactory.getLogger(InvoiceIssuerScheduler::class.java)

    @Scheduled(fixedRate = 3600, initialDelay = 5, timeUnit = TimeUnit.SECONDS)
    fun runJob() {
        logger.info("Sending invoice...")
        invoiceIssuerService.issueInvoice()
    }
}