package br.com.zup.academy.pix

import br.com.zup.academy.TipoDeConta
import org.hibernate.annotations.GenericGenerator
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
    val tipoChave: br.com.zup.academy.pix.TipoDeChave?,

    @field:NotBlank
    @Column(unique = true)
    val valorChave: String,

    @field:NotNull
    val tipoConta: TipoDeConta,

    @field:Valid
    val conta: Conta
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    @Column(nullable = false)
    val criadaEm: LocalDateTime = LocalDateTime.now()
}