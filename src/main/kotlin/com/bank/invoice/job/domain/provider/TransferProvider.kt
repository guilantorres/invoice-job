package com.bank.invoice.job.domain.provider

import com.bank.invoice.job.domain.Transfer

interface TransferProvider {
    fun create(transfer: Transfer): Transfer
}