package com.bank.invoice.job.infra.starkbank

import com.bank.invoice.job.domain.Transfer
import com.bank.invoice.job.domain.provider.TransferProvider
import com.starkbank.Project
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import com.starkbank.Transfer as SdkTransfer

@Component
@Profile("prod")
class StarkBankTransferProvider(
    private val project: Project
): TransferProvider {
    override fun create(transfer: Transfer): Transfer {
        val data = mapOf(
            "amount" to transfer.amount,
            "bankCode" to transfer.bankCode,
            "branchCode" to transfer.branchCode,
            "accountNumber" to transfer.accountNumber,
            "taxId" to transfer.taxId,
            "name" to transfer.name,
            "accountType" to transfer.accountType
        )

        val sdkTransfer = SdkTransfer(data)

        val createdTransfers = SdkTransfer.create(listOf(sdkTransfer), project)
        val createdTransfer = createdTransfers.first()

        return transfer.copy(
            id = createdTransfer.id,
            status = createdTransfer.status
        )
    }
}