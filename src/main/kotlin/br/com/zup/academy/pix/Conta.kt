package br.com.zup.academy.pix

import javax.persistence.Embeddable

@Embeddable
class Conta(
    val instituicao: Instituicao,
    val agencia: String,
    val numero: String,
    val titular: Titular
) {


}