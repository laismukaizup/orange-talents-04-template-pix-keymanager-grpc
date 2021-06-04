package br.com.zup.academy

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Get
import io.micronaut.http.client.annotation.Client

@Client("http://localhost:9091")
interface ItauClient {
    @Get("/api/v1/clientes/{clienteId}")
    fun consulta(clienteId : String): HttpResponse<ItauResponse>

}