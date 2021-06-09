package br.com.zup.academy.pix.remove

import br.com.zup.academy.RemoveChavePixGRPCServiceGrpc
import br.com.zup.academy.RemoveChavePixRequest
import br.com.zup.academy.pix.ChavePixRepository
import br.com.zup.academy.pix.modelo.*
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import javax.inject.Singleton

@MicronautTest(transactional = false)
internal class RemoveChaveEndpointTest(
    val repository: ChavePixRepository,
    val grpcClient : RemoveChavePixGRPCServiceGrpc.RemoveChavePixGRPCServiceBlockingStub
)
{
    @Test
    internal fun deveRemoverUmaChave() {
        val tipoChave = TipoDeChave.CPF
        val valorChave = "36967380850"
        val tipoConta = TipoDeConta.CONTA_CORRENTE
        val nomeInstituicao = "Itau"
        val isbp = "1234"
        val agencia = "456"
        val numero = "12345678-9"
        val cpfTitular = "36967380850"
        val idCliente = "5260263c-a3c1-4727-ae32-3bdb2538841b"

        val conta = Conta(Instituicao(nomeInstituicao, isbp), agencia, numero, Titular(cpfTitular))
        val save = repository.save(ChavePix(idCliente, tipoChave, valorChave, tipoConta, conta))

        val response = grpcClient.remover(
            RemoveChavePixRequest.newBuilder()
                .setClienteId(idCliente)
                .setPixId(save.id).build()
        )
        assertTrue(response.sucesso)
    }

    @Test
    internal fun deveRetornarFailedPreConditionCasoParametrosVazios() {

        val assertThrows = assertThrows<StatusRuntimeException> {
            val response = grpcClient.remover(
                RemoveChavePixRequest.newBuilder().build()
            )
        }
        assertEquals(Status.FAILED_PRECONDITION.code,assertThrows.status.code)
    }

    @Factory
    class Clients {
        @Replaces
        @Singleton
        fun blockStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): RemoveChavePixGRPCServiceGrpc.RemoveChavePixGRPCServiceBlockingStub? {
            return RemoveChavePixGRPCServiceGrpc.newBlockingStub(channel)
        }
    }
}