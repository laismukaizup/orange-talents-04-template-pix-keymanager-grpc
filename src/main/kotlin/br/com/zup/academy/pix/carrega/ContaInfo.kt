package br.com.zup.academy.pix.carrega

import br.com.zup.academy.pix.modelo.Instituicao

data class ContaInfo(
    val instituicao: String,
    val nomeDoTitular: String,
    val cpfDoTitular:String,
    val agencia: String,
    val numeroDaConta: String
) {
}