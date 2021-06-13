package br.com.zup.academy.pix.remove

import br.com.zup.academy.RemoveChavePixGRPCServiceGrpc
import br.com.zup.academy.RemoveChavePixRequest
import br.com.zup.academy.bancoCentral.BCBClient
import br.com.zup.academy.bancoCentral.DeletePixKeyRequest
import br.com.zup.academy.bancoCentral.DeletePixKeyResponse
import br.com.zup.academy.itau.ItauClient
import br.com.zup.academy.itau.TitularResponse
import br.com.zup.academy.pix.ChavePixRepository
import br.com.zup.academy.pix.modelo.*
import io.grpc.ManagedChannel
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest(transactional = false)
internal class RemoveChaveEndpointTest(
    val repository: ChavePixRepository,
    val grpcClient: RemoveChavePixGRPCServiceGrpc.RemoveChavePixGRPCServiceBlockingStub
) {
    @Inject
    lateinit var client: ItauClient

    @Inject
    lateinit var clientBCB: BCBClient

    lateinit var CHAVE_EXISTENTE: ChavePix

    @BeforeEach
    fun setup() {
        CHAVE_EXISTENTE = repository.save(
            ChavePix(
                "ae93a61c-0642-43b3-bb8e-a17072295955",
                TipoDeChave.EMAIL,
                "leonardo.silva@zup.com.br",
                TipoDeConta.CONTA_POUPANCA,
                Conta(

                    Instituicao(
                        "Itau",
                        "60701190"
                    ),
                    "0001",
                    "123456",
                    Titular(
                        "40764442058",
                        "Leonardo Silva",
                        "ae93a61c-0642-43b3-bb8e-a17072295955"
                    )
                )
            )
        )

        Mockito.`when`(
            client.consulta(CHAVE_EXISTENTE.idCliente)
        ).thenReturn(
            HttpResponse.ok(
                TitularResponse(
                    CHAVE_EXISTENTE.conta.titular.id,
                    CHAVE_EXISTENTE.conta.titular.nome,
                    CHAVE_EXISTENTE.conta.titular.cpf,
                    CHAVE_EXISTENTE.conta.instituicao
                )
            )
        )

        Mockito.`when`(
            clientBCB.deletaChave(
                CHAVE_EXISTENTE.valorChave,
                (DeletePixKeyRequest(CHAVE_EXISTENTE.valorChave))
            )
        )
            .thenReturn(
                HttpResponse.ok(
                    DeletePixKeyResponse(
                        CHAVE_EXISTENTE.valorChave, CHAVE_EXISTENTE.conta.instituicao.ispb,
                        LocalDateTime.now()
                    )
                )
            )
    }

    @AfterEach
    fun cleanUp() {
        repository.deleteAll()
    }

    @Test
    internal fun deveRemoverChavePix() {
        val responseRemove = grpcClient.remover(
            RemoveChavePixRequest.newBuilder()
                .setClienteId(CHAVE_EXISTENTE.idCliente)
                .setPixId(CHAVE_EXISTENTE.id)
                .build()
        )
        assertTrue(responseRemove.sucesso)

    }

    @MockBean(ItauClient::class)
    fun client(): ItauClient? {
        return Mockito.mock(ItauClient::class.java)
    }

    @MockBean(BCBClient::class)
    fun clientBCB(): BCBClient? {
        return Mockito.mock(BCBClient::class.java)
    }

    @Factory
    class Clients {
        @Replaces
        @Singleton
        fun blockStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel):
                RemoveChavePixGRPCServiceGrpc.RemoveChavePixGRPCServiceBlockingStub? {
            return RemoveChavePixGRPCServiceGrpc.newBlockingStub(channel)
        }
    }
}