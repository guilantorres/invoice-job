package com.bank.invoice.job.extension

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val STARK_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSxxx")

fun LocalDateTime.toStarkIsoFormat(): String {
    return this.atZone(ZoneId.systemDefault())
        .withZoneSameInstant(ZoneId.of("UTC"))
        .format(STARK_DATE_FORMATTER)
}