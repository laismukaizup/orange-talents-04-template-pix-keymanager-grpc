package br.com.zup.academy.pix.modelo

import javax.persistence.Embeddable

@Embeddable
class Titular(
    val cpf: String
) {

}
