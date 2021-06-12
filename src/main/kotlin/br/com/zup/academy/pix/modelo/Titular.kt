package br.com.zup.academy.pix.modelo

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class Titular(
    val cpf: String,
    val nome: String,
    val id: String
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val idTitular: Long? = null
}
