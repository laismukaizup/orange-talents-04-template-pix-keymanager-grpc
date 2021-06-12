package br.com.zup.academy.pix.modelo

import javax.persistence.*

@Entity
data class Instituicao(val nome: String, val ispb: String) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id :Long? = null
}
