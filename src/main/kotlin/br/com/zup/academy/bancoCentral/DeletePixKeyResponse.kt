package br.com.zup.academy.bancoCentral

import java.time.LocalDateTime

class DeletePixKeyResponse(
    val key: String,
    val participant: String,
    val deletedAt: LocalDateTime
)