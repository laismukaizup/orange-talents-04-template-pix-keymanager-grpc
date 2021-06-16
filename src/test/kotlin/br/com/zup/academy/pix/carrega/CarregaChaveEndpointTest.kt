package br.com.zup.academy.pix.carrega

import br.com.zup.academy.CarregaChavePixGRPCServiceGrpc
import br.com.zup.academy.CarregaChavePixRequest
import br.com.zup.academy.bancoCentral.*
import br.com.zup.academy.pix.ChavePixRepository
import br.com.zup.academy.pix.modelo.*
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest(transactional = false)
internal class CarregaChaveEndpointTest(
    val repository: ChavePixRepository,
    val grpcClient: CarregaChavePixGRPCServiceGrpc.CarregaChavePixGRPCServiceBlockingStub
) {

    @Inject
    lateinit var bcbClient: BCBClient;

    lateinit var CHAVE_EXISTENTE: ChavePix
    lateinit var conta: Conta

    @BeforeEach
    fun setup() {
        repository.deleteAll()

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
    }

    @AfterEach
    fun cleanUp() {
        repository.deleteAll()
    }


    @Test
    internal fun deveCarregarChavePorPixIdeClienteId() {

        val chaveExistente = repository.findByValorChave(CHAVE_EXISTENTE.valorChave).get()

        val response = grpcClient.carregar(
            CarregaChavePixRequest.newBuilder()
                .setPixId(
                    CarregaChavePixRequest.FiltroPorIdPix.newBuilder()
                        .setPixId(chaveExistente.id)
                        .setClienteId(chaveExistente.idCliente)
                        .build()
                )
                .build()
        )
        with(response) {
            assertEquals(chaveExistente.id, this.pidId)
            assertEquals(chaveExistente.idCliente, this.clienteId)
            assertEquals(chaveExistente.tipoChave!!.name, this.chave.tipo.name)
            assertEquals(chaveExistente.valorChave, this.chave.valorChave)
        }
    }

    @Test
    internal fun naoDeveCarregarChavePorPixIdeClienteIdQuandoFiltroInvalido() {
        val assertThrows = assertThrows<StatusRuntimeException> {
            grpcClient.carregar(
                CarregaChavePixRequest.newBuilder()
                    .setPixId(
                        CarregaChavePixRequest.FiltroPorIdPix.newBuilder()
                            .setPixId("")
                            .setClienteId("")
                            .build()
                    )
                    .setValorChave("").build()
            )
        }

        with(assertThrows) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
        }

    }

    @Test
    internal fun naoDeveCarregarChavePorPixIdeClienteIdQuandoRegistroNaoExistir() {

        val assertThrows = assertThrows<StatusRuntimeException> {
            grpcClient.carregar(
                CarregaChavePixRequest.newBuilder()
                    .setPixId(
                        CarregaChavePixRequest.FiltroPorIdPix.newBuilder()
                            .setPixId(UUID.randomUUID().toString())
                            .setClienteId(UUID.randomUUID().toString())
                            .build()
                    )
                    .build()
            )
        }
        with(assertThrows) {
            assertEquals(Status.NOT_FOUND.code, status.code)
        }
    }

    @Test
    internal fun deveCarregarChaveQuandoRegistroExistirLocalmente() {
        val chaveExistente = repository.findByValorChave(CHAVE_EXISTENTE.valorChave).get()

        val response = grpcClient.carregar(
            CarregaChavePixRequest.newBuilder()
                .setValorChave(chaveExistente.valorChave)
                .build()
        )
        with(response) {
            assertEquals(chaveExistente.id, this.pidId)
            assertEquals(chaveExistente.idCliente, this.clienteId)
            assertEquals(chaveExistente.tipoChave!!.name, this.chave.tipo.name)
            assertEquals(chaveExistente.valorChave, this.chave.valorChave)
        }
    }

    @Test
    internal fun deveCarregarChavePorValorQuandoRegistroNaoExistirLocalmenteMasExistirNoBCB() {
        val chaveBCB = "user.from.another.bank@santander.com.br"
        val bcbResponse = CreatePixKeyResponse(chaveBCB)
        Mockito.`when`(bcbClient.consultaChave(chaveBCB))
            .thenReturn(HttpResponse.ok(bcbResponse))

        val response = grpcClient.carregar(
            CarregaChavePixRequest.newBuilder()
                .setValorChave(chaveBCB)
                .build()
        )
        with(response) {
            assertEquals("", this.pidId)
            assertEquals("", this.clienteId)
            assertEquals(bcbResponse.keyType.name, this.chave.tipo.name)
            assertEquals(bcbResponse.key, this.chave.valorChave)
        }

    }

    @Test
    internal fun naoDeveCarregarChavePorValorQuandoRegistroNaoExistirLocalmenteNemExistirNoBCB() {
        val chaveBCB = "user.from.another.bank@santander.com.br"
        val bcbResponse = CreatePixKeyResponse(chaveBCB)
        Mockito.`when`(bcbClient.consultaChave(chaveBCB))
            .thenReturn(HttpResponse.notFound())

        val assertThrows = assertThrows<StatusRuntimeException> {
            grpcClient.carregar(
                CarregaChavePixRequest.newBuilder()
                    .setValorChave(chaveBCB)
                    .build()
            )
        }

        with(assertThrows) {
            assertEquals(Status.NOT_FOUND.code, status.code)
        }
    }

    @Test
    internal fun naoDeveCarregarChavePorValorQuandoFiltroInvalido() {
        val assertThrows = assertThrows<StatusRuntimeException> {
            grpcClient.carregar(
                CarregaChavePixRequest.newBuilder()
                    .setValorChave("").build()
            )
        }

        with(assertThrows) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
        }

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
                CarregaChavePixGRPCServiceGrpc.CarregaChavePixGRPCServiceBlockingStub? {
            return CarregaChavePixGRPCServiceGrpc.newBlockingStub(channel)
        }
    }


    private fun CreatePixKeyResponse(chaveBCB: String): CreatePixKeyResponse {
        return CreatePixKeyResponse(
            KeyType.EMAIL,
            chaveBCB,
            BankAccount(
                CHAVE_EXISTENTE.conta.instituicao.ispb,
                CHAVE_EXISTENTE.conta.agencia,
                CHAVE_EXISTENTE.conta.numero,
                AccountType.CACC
            ),
            Owner(OwnerType.NATURAL_PERSON, CHAVE_EXISTENTE.conta.titular.nome, CHAVE_EXISTENTE.conta.titular.cpf),
            LocalDateTime.now().toString()
        )
    }


}