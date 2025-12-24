package com.bank.invoice.job

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class InvoiceJobApplication

fun main(args: Array<String>) {
	runApplication<InvoiceJobApplication>(*args)
}
