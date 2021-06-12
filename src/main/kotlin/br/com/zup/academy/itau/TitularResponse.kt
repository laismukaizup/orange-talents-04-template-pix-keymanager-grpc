package br.com.zup.academy.itau

import br.com.zup.academy.pix.modelo.Instituicao
import br.com.zup.academy.pix.modelo.Titular

data class TitularResponse(
    val id: String,
    val nome: String,
    val cpf: String,
    val instituicao: Instituicao,
) {
    fun toModel(): Titular {
        return Titular(cpf, nome, id)
    }
}
