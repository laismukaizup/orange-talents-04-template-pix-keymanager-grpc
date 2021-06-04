package br.com.zup.academy

import javax.persistence.*
import javax.validation.constraints.NotBlank

@Entity
data class ChavePix(
    @field:NotBlank
    val idCliente: String,
    @field:NotBlank
    val tipoChave: TipoChave,
    @field:NotBlank
    @Column(unique = true)
    val valorChave: String,
    @field:NotBlank
    val tipoConta: TipoConta
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
}