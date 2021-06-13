package br.com.zup.academy.pix.cadastra

import br.com.zup.academy.ValidPixKey
import br.com.zup.academy.bancoCentral.*
import br.com.zup.academy.pix.*
import br.com.zup.academy.pix.modelo.ChavePix
import br.com.zup.academy.pix.modelo.Conta
import br.com.zup.academy.pix.modelo.TipoDeChave
import br.com.zup.academy.pix.modelo.TipoDeConta
import io.micronaut.core.annotation.Introspected
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size


@Introspected
@ValidPixKey
data class CadastraCPRequest(
    @ValidUUID
    @field:NotBlank
    val clienteId: String?,
    @field:NotNull
    val tipoChave: TipoDeChave?,
    @field:Size(max = 77)
    var valorChave: String?,
    @field:NotNull
    val tipoConta: TipoDeConta?
) {
    fun toModel(conta: Conta): ChavePix {
        return ChavePix(clienteId!!, tipoChave, valorChave!!, tipoConta!!, conta)
    }

    fun toCreatePixKeyRequest(conta: Conta): CreatePixKeyRequest {
        return CreatePixKeyRequest(
            tipoChave!!.converter().name,
            valorChave!!,
            BankAccount(
                conta.instituicao.ispb,
                conta.agencia,
                conta.numero,
                tipoConta!!.converter()
            ),
            Owner(OwnerType.LEGAL_PERSON, conta.titular.nome, conta.titular.cpf)
        )
    }

    fun validaValorChave(bcbResponse: CreatePixKeyResponse) {
        if (tipoChave!! == TipoDeChave.CHAVE_ALEATORIA)
            valorChave = bcbResponse.key
    }
}