package br.com.zup.academy.pix.cadastra

import br.com.zup.academy.RegistraChavePixRequest
import br.com.zup.academy.TipoDeChave
import br.com.zup.academy.TipoDeConta

fun RegistraChavePixRequest.toModel() : ChavePixRequest{
 return ChavePixRequest(clienteId = clienteId,
     tipoChave = when(tipoChave){ TipoDeChave.UNKNOWN_TIPO_CHAVE -> null
                                else ->  br.com.zup.academy.pix.TipoDeChave.valueOf(tipoChave.name)},
     valorChave = valorChave,
     tipoConta = when (tipoConta){TipoDeConta.UNKNOWN_TIPO_CONTA -> null
     else -> TipoDeConta.valueOf(tipoConta.name)}

 )
}