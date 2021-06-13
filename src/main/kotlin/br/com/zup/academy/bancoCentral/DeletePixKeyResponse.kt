package br.com.zup.academy.bancoCentral

import java.time.LocalDateTime

data class DeletePixKeyResponse(
    val key: String,
    val participant: String,
    val deletedAt: LocalDateTime
)