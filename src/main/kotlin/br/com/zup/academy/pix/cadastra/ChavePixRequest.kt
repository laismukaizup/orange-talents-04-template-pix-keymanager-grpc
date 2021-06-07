package br.com.zup.academy.pix.cadastra

import br.com.zup.academy.TipoDeConta
import br.com.zup.academy.ValidPixKey
import br.com.zup.academy.pix.ChavePix
import br.com.zup.academy.pix.Conta
import br.com.zup.academy.pix.ValidUUID
import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Introspected
@ValidPixKey
data class ChavePixRequest(
    @ValidUUID
    @field:NotBlank
    val clienteId: String?,
    @field:NotNull
    val tipoChave: br.com.zup.academy.pix.TipoDeChave?,
    @field:Size(max = 77)
    val valorChave: String?,
    @field:NotNull
    val tipoConta: br.com.zup.academy.pix.TipoDeConta?
) {
    fun toModel(conta: Conta): ChavePix {

        return ChavePix(clienteId!!, tipoChave, valorChave!!, tipoConta!!, conta)
    }
}