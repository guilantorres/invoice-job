package com.bank.invoice.job.application

import com.bank.invoice.job.domain.Transfer
import com.bank.invoice.job.domain.provider.TransferProvider
import com.bank.invoice.job.dto.WebhookInvoice
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class TransferService(
    private val transferProvider: TransferProvider
) {
    private val logger = LoggerFactory.getLogger(TransferService::class.java)

    fun processPaidInvoice(invoice: WebhookInvoice) {
        try {
            if (invoice.status != "paid") {
                logger.info("Invoice not paid: {}", invoice)
                return
            }
            val amountToTransfer = invoice.amount - invoice.fee
            if (amountToTransfer <= 0) {
                logger.warn("Amount to transfer should not be 0. amountToTransfer = $amountToTransfer")
                return
            }
            val newTransfer = createNewTransfer(amountToTransfer)
            val createdTransfer = transferProvider.create(newTransfer)
            logger.info("Created Transfer: {}", createdTransfer)
        } catch (e: Exception) {
            logger.error("Error while creating Transfer", e)
            throw e
        }
    }

    private fun createNewTransfer(amountToTransfer: Long): Transfer {
        return Transfer(
            amount = amountToTransfer,
            taxId = "20.018.183/0001-80",
            name = "Stark Bank S.A.",
            bankCode = "20018183",
            branchCode = "0001",
            accountNumber = "6341320293482496",
            accountType = "payment",
            created = LocalDateTime.now()
        )
    }
}