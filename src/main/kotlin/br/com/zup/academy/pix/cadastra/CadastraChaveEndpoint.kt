package br.com.zup.academy.pix.cadastra

import br.com.zup.academy.*
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CadastraChaveEndpoint(@Inject private val chavePixService: ChavePixService) : KeymanagerRegistraGrpcServiceGrpc.KeymanagerRegistraGrpcServiceImplBase() {

    override fun cadastrar(
        request: RegistraChavePixRequest,
        responseObserver: StreamObserver<RegistraChavePixResponse>
    ) {

        val chavePix = request.toModel()
        val chavePixRegistrada =  chavePixService.cadastra(chavePix)
        responseObserver.onNext(RegistraChavePixResponse.newBuilder()
            .setClienteId(chavePixRegistrada.idCliente)
            .setPixId(chavePixRegistrada.id.toString())
            .build())
        responseObserver.onCompleted()
    }
}