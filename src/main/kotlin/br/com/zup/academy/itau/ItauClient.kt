package br.com.zup.academy.itau

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.client.annotation.Client

    @Client("http://localhost:9091/api/v1/clientes")
interface ItauClient {
    @Get("/{clienteId}/contas")
    fun consulta(@PathVariable clienteId: String,@QueryValue tipo: String): HttpResponse<ContaResponse>

    @Get("/{clienteId}")
    fun consulta(@PathVariable clienteId: String): HttpResponse<TitularResponse>
}