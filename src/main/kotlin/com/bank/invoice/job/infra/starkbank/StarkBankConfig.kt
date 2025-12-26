package com.bank.invoice.job.infra.starkbank

import com.starkbank.Project
import com.starkbank.Settings
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class StarkBankConfig(
    @Value("\${starkbank.project.id}") private val projectId: String,
    @Value("\${starkbank.private.key}") private val privateKeyContent: String,
    @Value("\${starkbank.environment:sandbox}") private val environment: String
) {

    @Bean
    fun starkBankProject(): Project {
        val formattedKey = privateKeyContent.replace("\\n", "\n")

        val project = Project(environment, projectId, formattedKey)
        Settings.user = project
        return project
    }
}