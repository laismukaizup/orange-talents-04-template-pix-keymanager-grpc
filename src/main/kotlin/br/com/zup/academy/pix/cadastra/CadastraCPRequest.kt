package br.com.zup.academy.pix.cadastra

import br.com.zup.academy.ValidPixKey
import br.com.zup.academy.pix.*
import br.com.zup.academy.pix.modelo.ChavePix
import br.com.zup.academy.pix.modelo.Conta
import br.com.zup.academy.pix.modelo.TipoDeChave
import br.com.zup.academy.pix.modelo.TipoDeConta
import io.micronaut.core.annotation.Introspected
import java.util.*
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size


@Introspected
@ValidPixKey
data class CadastraCPRequest(
    @ValidUUID
    @field:NotNull
    val clienteId: UUID?,
    @field:NotNull
    val tipoChave: TipoDeChave?,
    @field:Size(max = 77)
    val valorChave: String?,
    @field:NotNull
    val tipoConta: TipoDeConta?
) {
    fun toModel(conta: Conta): ChavePix {

        return ChavePix(clienteId!!, tipoChave, valorChave!!, tipoConta!!, conta)
    }
}