package br.com.zup.academy.pix.carrega

import br.com.zup.academy.bancoCentral.BCBClient
import br.com.zup.academy.handler.ChavePixNaoEncontradaException
import br.com.zup.academy.pix.ChavePixRepository
import br.com.zup.academy.pix.ValidUUID
import io.micronaut.core.annotation.Introspected
import io.micronaut.http.HttpStatus
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Introspected
sealed class Filtro {

    abstract fun filtra(repository: ChavePixRepository, bcbClient: BCBClient): ChavePixInfo

    @Introspected
    data class PorPixId(
        @field:NotBlank @field:ValidUUID val clienteId: String,
        @field:NotBlank @field:ValidUUID val pixId: String,
    ) : Filtro() {
//        fun pixIdAsUuid() = UUID.fromString(pixId)
//        fun clienteIdAsUuid() = UUID.fromString(clienteId)

        override fun filtra(repository: ChavePixRepository, bcbClient: BCBClient): ChavePixInfo {
            return repository.findById(pixId)
                .map(ChavePixInfo::of)
                .orElseThrow { ChavePixNaoEncontradaException("chave pix não encontrada") }
        }
    }

    @Introspected
    class PorChave(
        @field:NotBlank @Size(max = 77) val valorChave: String
    ) : Filtro() {

        override fun filtra(repository: ChavePixRepository, bcbClient: BCBClient): ChavePixInfo {
            return repository.findByValorChave(valorChave)
                .map(ChavePixInfo::of)
                .orElseGet {
                    val response = bcbClient.consultaChave(valorChave)
                    when (response.status) {
                        HttpStatus.OK -> response.body()?.toModel()
                        else -> throw ChavePixNaoEncontradaException("chave pix não encontrada")
                    }
                }
        }
    }

    @Introspected
    class Invalido() : Filtro() {
        override fun filtra(repository: ChavePixRepository, bcbClient: BCBClient): ChavePixInfo {
            TODO("Not yet implemented")
        }
    }
}