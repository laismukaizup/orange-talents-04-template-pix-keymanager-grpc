package br.com.zup.academy.pix

import br.com.zup.academy.TipoDeChave
import br.com.zup.academy.TipoDeConta
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
    @field:NotBlank
    val idCliente: String,

    @field:NotNull
    val tipoChave: br.com.zup.academy.pix.TipoDeChave?,

    @field:NotBlank
    @Column(unique = true)
    val valorChave: String,

    @field:NotNull
    val tipoConta: br.com.zup.academy.pix.TipoDeConta,

    @field:Valid
    val conta: Conta
) {
    @Id
    @GeneratedValue
    val pixId: UUID? = null

    @Column(nullable = false)
    val criadaEm: LocalDateTime = LocalDateTime.now()
}