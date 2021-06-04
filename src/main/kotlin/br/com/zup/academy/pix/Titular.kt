package br.com.zup.academy.pix

import javax.persistence.Embeddable

@Embeddable
class Titular(
    val cpf: String
) {

}
