package br.com.zup.academy.pix.modelo

import java.time.LocalDateTime
import java.util.*
import javax.persistence.*
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Entity
data class ChavePix(
    @field:NotBlank
    val idCliente: String,

    @field:NotNull
    val tipoChave: TipoDeChave?,

    @field:NotBlank
    @Column(unique = true)
    val valorChave: String,

    @field:NotNull
    val tipoConta: TipoDeConta,

    @field:Valid
    @field:ManyToOne(cascade =  arrayOf(CascadeType.PERSIST))
    val conta: Conta
) {
    @Id
    val id: String? = UUID.randomUUID().toString()

    @Column(nullable = false)
    val criadaEm: LocalDateTime = LocalDateTime.now()
}