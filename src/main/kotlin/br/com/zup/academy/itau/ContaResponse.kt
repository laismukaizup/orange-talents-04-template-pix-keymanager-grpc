package br.com.zup.academy.itau

import br.com.zup.academy.pix.Conta
import br.com.zup.academy.pix.Instituicao
import br.com.zup.academy.pix.Titular

data class ContaResponse(
    val instituicao: Instituicao,
    val agencia: String,
    val numero: String,
    val titular: Titular
) {
    fun toModel(): Conta {
        return Conta(instituicao, agencia, numero, titular)
    }
}
