package com.bank.invoice.job.infra.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface IssuedInvoiceRepository : JpaRepository<IssuedInvoice, String>