package br.com.zup.academy.pix.lista

import br.com.zup.academy.pix.ValidUUID
import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotNull

@Introspected
data class ListaCPRequest(
    @ValidUUID
    @field:NotNull
    val clienteId: String
)