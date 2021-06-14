package br.com.zup.academy.pix.carrega

import br.com.zup.academy.pix.modelo.ChavePix
import br.com.zup.academy.pix.modelo.Conta
import br.com.zup.academy.pix.modelo.TipoDeChave
import br.com.zup.academy.pix.modelo.TipoDeConta
import java.time.LocalDateTime

data class ChavePixInfo(
    val pixId: String? = null,
    val clienteId: String? = null,
    val tipoDeChave: TipoDeChave,
    val valorChave: String,
    val tipoDeConta: TipoDeConta,
    val conta: ContaInfo,
    val registradaEm: LocalDateTime = LocalDateTime.now()
) {
    companion object {
        fun of(chave: ChavePix): ChavePixInfo {
            return ChavePixInfo(
                chave.id,
                chave.idCliente,
                chave.tipoChave!!,
                chave.valorChave,
                chave.tipoConta,
                ContaInfo(chave.conta.instituicao.nome,
                chave.conta.titular.nome,
                chave.conta.titular.cpf,
                chave.conta.agencia,
                chave.conta.numero),
                chave.criadaEm
            )
        }
    }
}