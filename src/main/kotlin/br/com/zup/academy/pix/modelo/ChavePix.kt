package br.com.zup.academy.pix.modelo

import java.time.LocalDateTime
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Entity
data class ChavePix(
    @field:NotNull
    val idCliente: UUID,

    @field:NotNull
    val tipoChave: TipoDeChave?,

    @field:NotBlank
    @Column(unique = true)
    val valorChave: String,

    @field:NotNull
    val tipoConta: TipoDeConta,

    @field:Valid
    val conta: Conta
) {
    @Id
    @GeneratedValue
    val id: UUID? = null

    @Column(nullable = false)
    val criadaEm: LocalDateTime = LocalDateTime.now()
}