package br.com.zup.academy.pix.cadastra

import br.com.zup.academy.CadastraChavePixGRPCServiceGrpc
import br.com.zup.academy.RegistraChavePixRequest
import br.com.zup.academy.RegistraChavePixResponse
import br.com.zup.academy.handler.ErrorHandler
import br.com.zup.academy.pix.ChavePixRepository
import br.com.zup.academy.pix.ChavePixService
import io.grpc.Status
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.inject.Singleton
import javax.validation.ConstraintViolationException

@Singleton
@ErrorHandler
class CadastraChaveEndpoint(
    @Inject val repository: ChavePixRepository,
    @Inject private val chavePixService: ChavePixService
) : CadastraChavePixGRPCServiceGrpc.CadastraChavePixGRPCServiceImplBase() {

    override fun cadastrar(
        request: RegistraChavePixRequest,
        responseObserver: StreamObserver<RegistraChavePixResponse>
    ) {
        try {
            val chavePix = request.toModel()
            val chavePixRegistrada = chavePixService.cadastra(chavePix)

            responseObserver.onNext(
                RegistraChavePixResponse.newBuilder()
                    .setClienteId(chavePixRegistrada.idCliente.toString())
                    .setPixId(chavePixRegistrada.id.toString())
                    .build()
            )
            responseObserver.onCompleted()
        } catch (e: ConstraintViolationException) {
            responseObserver.onError(
                Status.INVALID_ARGUMENT
                    .withDescription(e.message)
                    .withCause(e.cause)
                    .asRuntimeException()
            )
        }
    }
}