package br.com.zup.academy.itau

import br.com.zup.academy.pix.modelo.Conta
import br.com.zup.academy.pix.modelo.Instituicao
import br.com.zup.academy.pix.modelo.TipoDeConta
import br.com.zup.academy.pix.modelo.Titular

data class ContaResponse(
    val tipo: TipoDeConta,
    val instituicao: Instituicao,
    val agencia: String,
    val numero: String,
    val titular: Titular
) {
    fun toModel(): Conta {
        return Conta(instituicao, agencia, numero, titular)
    }
}
