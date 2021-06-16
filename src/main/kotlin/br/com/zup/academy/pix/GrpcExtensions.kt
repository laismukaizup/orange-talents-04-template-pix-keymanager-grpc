package br.com.zup.academy.pix.cadastra

import br.com.zup.academy.*
import br.com.zup.academy.CarregaChavePixRequest.FiltroCase.*
import br.com.zup.academy.pix.carrega.Filtro
import br.com.zup.academy.pix.lista.ListaCPRequest
import br.com.zup.academy.pix.modelo.TipoDeChave
import br.com.zup.academy.pix.modelo.TipoDeConta
import br.com.zup.academy.pix.remove.RemoveCPRequest
import java.util.*
import javax.validation.ConstraintViolationException
import javax.validation.Validator

fun RegistraChavePixRequest.toModel(): CadastraCPRequest {
    return CadastraCPRequest(
        clienteId = clienteId,
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
    return RemoveCPRequest(clienteId, pixId)
}

fun CarregaChavePixRequest.toModel(validator : Validator): Filtro{
    val filtro = when (filtroCase){
        PIXID -> pixId.let { Filtro.PorPixId(it.clienteId, it.pixId) }
        VALORCHAVE -> Filtro.PorChave(valorChave)
        FILTRO_NOT_SET -> Filtro.Invalido()
    }
    val violations = validator.validate(filtro)
    if(violations.isNotEmpty()){
        throw ConstraintViolationException(violations)
    }

    return filtro
}

fun ListaChavePixRequest.toModel(): ListaCPRequest {
    return ListaCPRequest(clienteId)
}