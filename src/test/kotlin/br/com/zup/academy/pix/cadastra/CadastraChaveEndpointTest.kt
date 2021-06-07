package br.com.zup.academy.pix.cadastra

import br.com.zup.academy.KeymanagerRegistraGrpcServiceGrpc
import br.com.zup.academy.RegistraChavePixRequest
import br.com.zup.academy.TipoDeChave
import br.com.zup.academy.TipoDeConta
import br.com.zup.academy.itau.ContaResponse
import br.com.zup.academy.itau.ItauClient
import br.com.zup.academy.pix.*
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest(transactional = false)
internal class CadastraChaveEndpointTest(
    val repository: ChavePixRepository,
    val grpcClient: KeymanagerRegistraGrpcServiceGrpc.KeymanagerRegistraGrpcServiceBlockingStub
) {

    @Inject
    lateinit var client: ItauClient

    @BeforeEach
    fun setup() {
        repository.deleteAll()
    }

    @Test
    internal fun deveInserirUmaChaveNoBanco() {
        val idCliente = "c56dfef4-7901-44fb-84e2-a2cefb157890"
        val tipoChave = TipoDeChave.CPF
        val valorChave = "36967380850"
        val tipoConta = TipoDeConta.CONTA_CORRENTE
        val nomeInstituicao = "Itau"
        val isbp = "1234"
        val agencia = "456"
        val numero = "12345678-9"
        val cpfTitular = "36967380850"

        `when`(client.consulta(idCliente, tipoConta.name)).thenReturn(
            HttpResponse.ok(
                ContaResponse(
                    Instituicao(nomeInstituicao, isbp),
                    agencia,
                    numero,
                    Titular(cpfTitular)
                )
            )
        )

        val response = grpcClient.cadastrar(
            RegistraChavePixRequest.newBuilder()
                .setClienteId(idCliente)
                .setTipoChave(tipoChave)
                .setValorChave(valorChave)
                .setTipoConta(tipoConta).build()
        )
        Assertions.assertNotNull(response.pixId)
    }

    @Test
    internal fun naoDeveInserirUmaChaveQuandoEncontarValorJaExistente() {
        val idCliente = "c56dfef4-7901-44fb-84e2-a2cefb157890"
        val tipoChave = br.com.zup.academy.pix.TipoDeChave.CPF
        val tipoChave2 = br.com.zup.academy.TipoDeChave.CPF
        val valorChave = "36967380850"
        val tipoConta = br.com.zup.academy.pix.TipoDeConta.CONTA_CORRENTE
        val tipoConta2 = br.com.zup.academy.TipoDeConta.CONTA_CORRENTE
        val nomeInstituicao = "Itau"
        val isbp = "1234"
        val agencia = "456"
        val numero = "12345678-9"
        val cpfTitular = "36967380850"

        val conta = Conta(Instituicao(nomeInstituicao, isbp), agencia, numero, Titular(cpfTitular))
        val chavePixSalvo = repository.save(ChavePix(idCliente, tipoChave, valorChave, tipoConta, conta))

        val response = assertThrows<StatusRuntimeException> {
            grpcClient.cadastrar(
                RegistraChavePixRequest.newBuilder()
                    .setClienteId(idCliente)
                    .setTipoChave(tipoChave2)
                    .setValorChave(valorChave)
                    .setTipoConta(tipoConta2).build()
            )

        }

        with(response) {
            Assertions.assertEquals(Status.ALREADY_EXISTS.code, status.code)

        }
    }

    @Test
    internal fun NaoDeveInserirUmaChaveQuandoNaoEncontrarDadoaDaConta() {
        val idCliente = "c56dfef4-7901-44fb-84e2-a2cefb157890"
        val tipoChave = br.com.zup.academy.pix.TipoDeChave.CPF
        val tipoChave2 = br.com.zup.academy.TipoDeChave.CPF
        val valorChave = "36967380850"
        val tipoConta = br.com.zup.academy.pix.TipoDeConta.CONTA_CORRENTE
        val tipoConta2 = br.com.zup.academy.TipoDeConta.CONTA_CORRENTE
        val nomeInstituicao = "Itau"
        val isbp = "1234"
        val agencia = "456"
        val numero = "12345678-9"
        val cpfTitular = "36967380850"

        `when`(client.consulta(idCliente, tipoConta.name)).thenReturn(HttpResponse.notFound())

        val response = assertThrows<StatusRuntimeException> {
            grpcClient.cadastrar(
                RegistraChavePixRequest.newBuilder()
                    .setClienteId(idCliente)
                    .setTipoChave(tipoChave2)
                    .setValorChave(valorChave)
                    .setTipoConta(tipoConta2).build()
            )
        }

        with(response) {
            Assertions.assertEquals(Status.FAILED_PRECONDITION.code, status.code)
        }
    }

    @Test
    internal fun naoDeveInserirUmaChaveQuandoParametrosForemInvalidos() {

        val assertThrows =
            assertThrows<StatusRuntimeException> { grpcClient.cadastrar(RegistraChavePixRequest.newBuilder().build()) }

        with(assertThrows) {
            Assertions.assertEquals(Status.INVALID_ARGUMENT.code, status.code)
        }
    }

    @MockBean(ItauClient::class)
    fun client(): ItauClient? {
        return Mockito.mock(ItauClient::class.java)
    }

    @Factory
    class Clients {
        @Singleton
        fun blockStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): KeymanagerRegistraGrpcServiceGrpc.KeymanagerRegistraGrpcServiceBlockingStub? {
            return KeymanagerRegistraGrpcServiceGrpc.newBlockingStub(channel)
        }
    }

}

