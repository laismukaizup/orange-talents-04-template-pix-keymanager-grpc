package br.com.zup.academy.pix.lista

import br.com.zup.academy.ListaChavePixGRPCServiceGrpc
import br.com.zup.academy.ListaChavePixRequest
import br.com.zup.academy.pix.ChavePixRepository
import br.com.zup.academy.pix.modelo.*
import io.grpc.ManagedChannel
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.*
import java.util.*
import javax.inject.Singleton

@MicronautTest(transactional = false)
internal class ListaChaveEndpointTest(
    val repository: ChavePixRepository,
    val grpcClient: ListaChavePixGRPCServiceGrpc.ListaChavePixGRPCServiceBlockingStub
) {


    lateinit var CLIENTE_ID: String

    @BeforeEach
    fun setup() {
        repository.deleteAll()
        CLIENTE_ID = UUID.randomUUID().toString()
        var i = 0;
        while (i < 3) {
            repository.save(
                ChavePix(
                    CLIENTE_ID,
                    TipoDeChave.EMAIL,
                    "leonardo_0${i}@zup.com.br",
                    TipoDeConta.CONTA_POUPANCA,
                    Conta(
                        Instituicao(
                            "Itau",
                            "60701190"
                        ),
                        "000${i}",
                        "12345${i}",
                        Titular(
                            "40764442058",
                            "Leonardo Silva",
                            CLIENTE_ID
                        )
                    )
                )
            )

            i++;
        }
    }

    @AfterEach
    fun cleanUp() {
        repository.deleteAll()
    }

    @Test
    internal fun deveCarregarListaPorIdCliente() {

        val response = grpcClient.listar(
            ListaChavePixRequest.newBuilder()
                .setClienteId(CLIENTE_ID).build()
        )
        with(response) {
            Assertions.assertEquals(3, response.chavesCount)
        }
    }

    @Test
    internal fun naoDeveListarChavesQuandoClienteNaoTiverChaves() {
        val response = grpcClient.listar(
            ListaChavePixRequest.newBuilder()
                .setClienteId(UUID.randomUUID().toString()).build()
        )
        with(response) {
            Assertions.assertEquals(0, response.chavesCount)
        }
    }

    @Test
    internal fun naoDeveCarregarListaQuandoIdClienteInexistente() {
        assertThrows<StatusRuntimeException> {
            grpcClient.listar(
                ListaChavePixRequest.newBuilder()
                    .setClienteId("").build()
            )
        }

    }

    @Factory
    class Clients {
        @Replaces
        @Singleton
        fun blockStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel):
                ListaChavePixGRPCServiceGrpc.ListaChavePixGRPCServiceBlockingStub? {
            return ListaChavePixGRPCServiceGrpc.newBlockingStub(channel)
        }
    }


}