package br.com.zup.academy.pix.remove

import br.com.zup.academy.*
import br.com.zup.academy.handler.ErrorHandler
import br.com.zup.academy.pix.ChavePixRepository
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import javax.inject.Singleton

@MicronautTest(transactional = false)
internal class RemoveChaveEndpointTest(
    val grpcClient : RemoveChavePixGRPCServiceGrpc.RemoveChavePixGRPCServiceBlockingStub
)
{

    @Test
    internal fun deveRemoverUmaChave() {

        val idCliente = "5260263c-a3c1-4727-ae32-3bdb2538841b"
        val pixId = "8eaeedaf-c83d-4e93-9d58-a998ae0ced38"
        val response = grpcClient.remover(
            RemoveChavePixRequest.newBuilder()
                .setClienteId(idCliente)
                .setPixId(pixId).build()
        )
        Assertions.assertTrue(response.sucesso)
    }

    @Test
    internal fun deveRetornarNotFoudCasoChaveNaoEncontrada() {

        val idCliente = "c56dfef4-7901-44fb-84e2-a2cefb157890"
        val pixId = "eab1d1fc-d388-4ff1-bbb7-be7847cabd0b"
        val assertThrows = assertThrows<StatusRuntimeException> {
            val response = grpcClient.remover(
                RemoveChavePixRequest.newBuilder()
                    .setClienteId(idCliente)
                    .setPixId(pixId).build()
            )
        }
        assertEquals(Status.NOT_FOUND.code,assertThrows.status.code)
    }

    @Test
    internal fun deveRetornarNotFoundCasoParametrosVazios() {
        val assertThrows = assertThrows<StatusRuntimeException> {
            val response = grpcClient.remover(
                RemoveChavePixRequest.newBuilder().build()
            )
        }
        assertEquals(Status.NOT_FOUND.code,assertThrows.status.code)
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