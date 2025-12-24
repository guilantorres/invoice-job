package com.bank.invoice.job.domain

enum class InvoiceStatus(val code: String) {
    CREATED("created"),
    PAID("paid"),
    CANCELED("canceled"),
    OVERDUE("overdue"),
    VOIDED("voided"),
    UNKNOWN("unknown");

    companion object {
        fun fromCode(code: String?): InvoiceStatus {
            return entries.find { it.code.equals(code, ignoreCase = true) } ?: UNKNOWN
        }
    }
}