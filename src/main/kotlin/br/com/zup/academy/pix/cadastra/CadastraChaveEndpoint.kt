package br.com.zup.academy.pix.cadastra

import br.com.zup.academy.KeymanagerRegistraGrpcServiceGrpc
import br.com.zup.academy.RegistraChavePixRequest
import br.com.zup.academy.RegistraChavePixResponse
import br.com.zup.academy.handler.ErrorHandler
import br.com.zup.academy.pix.ChavePixRepository
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
) :
    KeymanagerRegistraGrpcServiceGrpc.KeymanagerRegistraGrpcServiceImplBase() {

    override fun cadastrar(
        request: RegistraChavePixRequest,
        responseObserver: StreamObserver<RegistraChavePixResponse>
    ) {
        try {

            if (repository.existsByValorChave(request.valorChave)) {
                responseObserver.onError(
                    Status.ALREADY_EXISTS
                        .withDescription("valor da chave já cadastrada")
                        .asRuntimeException()
                )
                return
            }
            val chavePix = request.toModel()
            val chavePixRegistrada = chavePixService.cadastra(chavePix)

            responseObserver.onNext(
                RegistraChavePixResponse.newBuilder()
                    .setClienteId(chavePixRegistrada.idCliente)
                    .setPixId(chavePixRegistrada.pixId.toString())
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