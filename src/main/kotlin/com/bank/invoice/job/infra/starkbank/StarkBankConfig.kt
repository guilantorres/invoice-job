package com.bank.invoice.job.infra.starkbank

import com.starkbank.Project
import com.starkbank.Settings
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
class StarkBankConfig(
    @Value("\${starkbank.project.id}") private val projectId: String,
    @Value("\${starkbank.private.key}") private val privateKeyContent: String,
    @Value("\${starkbank.environment:sandbox}") private val environment: String
) {
    @Bean
    fun starkBankProject(): Project {
        val key = privateKeyContent
            .replace("-----BEGIN EC PRIVATE KEY-----", "")
            .replace("-----END EC PRIVATE KEY-----", "")
            .replace("\\n", "")
            .replace(" ", "")
            .trim()

        val privateKey = "-----BEGIN EC PRIVATE KEY-----\n$key\n-----END EC PRIVATE KEY-----"

        val project = Project(environment, projectId, privateKey)
        Settings.user = project
        return project
    }
}