package br.com.zup.academy.pix.remove

import br.com.zup.academy.RemoveChavePixGRPCServiceGrpc
import br.com.zup.academy.RemoveChavePixRequest
import br.com.zup.academy.RemoveChavePixResponse
import br.com.zup.academy.handler.ErrorHandler
import br.com.zup.academy.pix.ChavePixService
import br.com.zup.academy.pix.cadastra.toModel
import io.grpc.Status
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.inject.Singleton
import javax.validation.ConstraintViolationException

@ErrorHandler
@Singleton
class RemoveChaveEndpoint(@Inject private val chavePixService: ChavePixService) :
    RemoveChavePixGRPCServiceGrpc.RemoveChavePixGRPCServiceImplBase() {

    override fun remover(request: RemoveChavePixRequest, responseObserver: StreamObserver<RemoveChavePixResponse>) {
        try {

            val requestCP = request.toModel()
            val sucesso = chavePixService.remove(requestCP)

            responseObserver.onNext(
                RemoveChavePixResponse.newBuilder()
                    .setSucesso(sucesso)
                    .build()
            )
            responseObserver.onCompleted()

        } catch (e: ConstraintViolationException) {
            responseObserver.onError(
                Status.INVALID_ARGUMENT
                    .withDescription(e.message)
                    .asRuntimeException()
            )
        }
    }
}