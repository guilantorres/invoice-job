package com.bank.invoice.job

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class InvoiceJobApplication

fun main(args: Array<String>) {
	runApplication<InvoiceJobApplication>(*args)
}
