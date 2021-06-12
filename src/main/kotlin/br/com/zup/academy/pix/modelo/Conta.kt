package br.com.zup.academy.pix.modelo

import javax.persistence.*

@Entity
data class Conta(
    @field:ManyToOne(cascade =  arrayOf(CascadeType.PERSIST))
    val instituicao: Instituicao,
    val agencia: String,
    val numero: String,
    @field:ManyToOne(cascade =  arrayOf(CascadeType.PERSIST))
    val titular: Titular
) {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id :Long? = null

}