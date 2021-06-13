package br.com.zup.academy.pix.cadastra

import br.com.zup.academy.CadastraChavePixGRPCServiceGrpc
import br.com.zup.academy.RegistraChavePixRequest
import br.com.zup.academy.TipoDeChaveGRPC
import br.com.zup.academy.TipoDeContaGRPC
import br.com.zup.academy.bancoCentral.*
import br.com.zup.academy.itau.ContaResponse
import br.com.zup.academy.itau.ItauClient
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
import org.junit.jupiter.api.*
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest(transactional = false)
internal class CadastraChaveEndpointTest(
    val repository: ChavePixRepository,
    val grpcClient: CadastraChavePixGRPCServiceGrpc.CadastraChavePixGRPCServiceBlockingStub
) {

    @Inject
    lateinit var client: ItauClient
    @Inject
    lateinit var clientBCB: BCBClient

    lateinit var CHAVE_REQUEST: CadastraCPRequest
    lateinit var CONTA_RESPONSE: ContaResponse

    lateinit var CREATE_PIX_KEY_REQUEST: CreatePixKeyRequest
    lateinit var CREATE_PIX_KEY_RESPONSE: CreatePixKeyResponse


    @BeforeEach
    fun setup() {
        CHAVE_REQUEST = CadastraCPRequest(
            "ae93a61c-0642-43b3-bb8e-a17072295955",
            TipoDeChave.CPF,
            "40764442058",
            TipoDeConta.CONTA_POUPANCA
        )
        CONTA_RESPONSE = ContaResponse(
            TipoDeConta.CONTA_POUPANCA,
            Instituicao("ITAÚ UNIBANCO S.A.", "60701190"),
            "0001",
            "125987",
            Titular("40764442058", "Leonardo Silva", "ae93a61c-0642-43b3-bb8e-a17072295955")
        )

        `when`(
            client.consulta(CHAVE_REQUEST.clienteId!!, CHAVE_REQUEST.tipoConta!!.name)
        ).thenReturn(
            HttpResponse.ok(CONTA_RESPONSE)
        )
        CREATE_PIX_KEY_REQUEST = CreatePixKeyRequest(
            CHAVE_REQUEST.tipoChave!!.converter().name,
            CHAVE_REQUEST.valorChave!!,
            BankAccount(
                CONTA_RESPONSE.instituicao.ispb,
                CONTA_RESPONSE.agencia,
                CONTA_RESPONSE.numero,
                CHAVE_REQUEST.tipoConta!!.converter()
            ),
            Owner(
                OwnerType.NATURAL_PERSON,
                CONTA_RESPONSE.titular.nome,
                CONTA_RESPONSE.titular.cpf
            )
        )

        CREATE_PIX_KEY_RESPONSE = CreatePixKeyResponse(
            CHAVE_REQUEST.tipoChave!!.converter(),
            CHAVE_REQUEST.valorChave!!,
            BankAccount(
                CONTA_RESPONSE.instituicao.ispb,
                CONTA_RESPONSE.agencia,
                CONTA_RESPONSE.numero,
                CHAVE_REQUEST.tipoConta!!.converter()
            ),
            Owner(
                OwnerType.NATURAL_PERSON,
                CONTA_RESPONSE.titular.nome,
                CONTA_RESPONSE.titular.cpf

            ),
            LocalDateTime.now().toString()
        )

        `when`(
            clientBCB.enviaRegistro(CREATE_PIX_KEY_REQUEST)
        ).thenReturn(HttpResponse.ok(CREATE_PIX_KEY_RESPONSE))
    }

    @AfterEach
    fun cleanUp() {
        repository.deleteAll()
    }


    @Test
    internal fun deveInserirUmaChaveNoBanco() {

        val response = grpcClient.cadastrar(
            RegistraChavePixRequest.newBuilder()
                .setClienteId(CHAVE_REQUEST.clienteId)
                .setTipoChave(CHAVE_REQUEST.tipoChave!!.converterToGRPCObject())
                .setValorChave(CHAVE_REQUEST.valorChave)
                .setTipoConta(CHAVE_REQUEST.tipoConta!!.converterToGrpcObject()).build()
        )
        Assertions.assertNotNull(response.pixId)
    }

    @Test
    internal fun naoDeveInserirUmaChaveQuandoEncontarValorJaExistente() {
        val idCliente = "c56dfef4-7901-44fb-84e2-a2cefb157890"
        val tipoChave = TipoDeChave.CPF
        val tipoChave2 = TipoDeChaveGRPC.CPF
        val valorChave = "36967380850"
        val tipoConta = TipoDeConta.CONTA_CORRENTE
        val tipoConta2 = TipoDeContaGRPC.CONTA_CORRENTE
        val nomeInstituicao = "Itau"
        val isbp = "1234"
        val agencia = "456"
        val numero = "12345678-9"
        val cpfTitular = "36967380850"

        val conta = Conta(
            Instituicao(nomeInstituicao, isbp), agencia, numero,
            Titular(cpfTitular, "Titular", idCliente)
        )
        repository.save(ChavePix(idCliente, tipoChave, valorChave, tipoConta, conta))

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
        val tipoChave2 = TipoDeChaveGRPC.CPF
        val valorChave = "36967380850"
        val tipoConta = TipoDeConta.CONTA_CORRENTE
        val tipoConta2 = TipoDeContaGRPC.CONTA_CORRENTE

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

    @MockBean(BCBClient::class)
    fun clientBCB(): BCBClient? {
        return Mockito.mock(BCBClient::class.java)
    }

    @Factory
    class Clients {
        @Replaces
        @Singleton
        fun blockStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): CadastraChavePixGRPCServiceGrpc.CadastraChavePixGRPCServiceBlockingStub? {
            return CadastraChavePixGRPCServiceGrpc.newBlockingStub(channel)
        }
    }
}

