package br.com.zup.academy.pix.cadastra

import br.com.zup.academy.RegistraChavePixRequest
import br.com.zup.academy.RemoveChavePixRequest
import br.com.zup.academy.TipoDeChaveGRPC
import br.com.zup.academy.TipoDeContaGRPC
import br.com.zup.academy.pix.modelo.TipoDeChave
import br.com.zup.academy.pix.modelo.TipoDeConta
import br.com.zup.academy.pix.remove.RemoveCPRequest
import java.util.*

fun RegistraChavePixRequest.toModel(): CadastraCPRequest {
    return CadastraCPRequest(
        clienteId = UUID.fromString(clienteId),
        tipoChave = when (tipoChave) {
            TipoDeChaveGRPC.UNKNOWN_TIPO_CHAVE -> null
            else -> TipoDeChave.valueOf(tipoChave.name)
        },
        valorChave = valorChave,
        tipoConta = when (tipoConta) {
            TipoDeContaGRPC.UNKNOWN_TIPO_CONTA -> null
            else -> TipoDeConta.valueOf(tipoConta.name)
        }

    )
}
fun RemoveChavePixRequest.toModel(): RemoveCPRequest {
    return RemoveCPRequest(UUID.fromString(clienteId), UUID.fromString(pixId))
}