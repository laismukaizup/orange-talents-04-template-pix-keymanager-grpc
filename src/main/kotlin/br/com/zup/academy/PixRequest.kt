package br.com.zup.academy

import javax.validation.constraints.NotBlank

data class PixRequest(
    @field:NotBlank
    val idCliente: String,
    @field:NotBlank
    val tipoChave: TipoChave,
    @field:NotBlank
    val valorChave: String,
    @field:NotBlank
    val tipoConta: TipoConta
)
{
}
