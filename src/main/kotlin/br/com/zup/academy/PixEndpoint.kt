package br.com.zup.academy

import com.google.protobuf.Any
import com.google.rpc.Code
import io.grpc.Status
import io.grpc.stub.StreamObserver
import io.micronaut.http.HttpResponse
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PixEndpoint(
    @Inject val repository: ChavePixRepository,
    @Inject val itauClient: ItauClient
) : PixGrpcServiceGrpc.PixGrpcServiceImplBase() {

    override fun cadastrar(request: PixGrpcRequest, responseObserver: StreamObserver<PixGrpcResponse>) {

        val itauResponse = itauClient.consulta(request.idCliente)
        if (itauResponse.status.code==404) {
            val statusProto = com.google.rpc.Status.newBuilder()
                .setCode(Code.NOT_FOUND.number)
                .setMessage("dados de entrada inválido")
                .addDetails(
                    Any.pack(
                        ErrorDetails.newBuilder()
                            .setCode(404)
                            .setMessage("id do cliente inválido")
                            .build()
                    )
                )
                .build()

            val e = io.grpc.protobuf.StatusProto
                .toStatusRuntimeException(statusProto)
            responseObserver.onError(e)
            return
        }


        if (repository.existsByValorChave(request.valorChave)) {
            responseObserver.onError(
                Status.ALREADY_EXISTS
                    .withDescription("valor da chave já cadastrada")
                    .asRuntimeException()
            )
            return
        }

        var valorChave = request.valorChave;
        if (request.tipoChave.equals(TipoChave.CPF) && (!valorChave.matches("^[0-9]{11}\$".toRegex()))) {
            responseObserver.onError(
                Status.INVALID_ARGUMENT
                    .withDescription("formato inválido - Ex de formato válido: 12345678901")
                    .asRuntimeException()
            )
            return
        } else if (request.tipoChave.equals(TipoChave.CELULAR) && (!valorChave.matches("^\\+[1-9][0-9]\\d{1,14}\$".toRegex()))) {
            responseObserver.onError(
                Status.INVALID_ARGUMENT
                    .withDescription("formato inválido - Ex de formato válido: +5585988714077")
                    .asRuntimeException()
            )
            return
        } else if (request.tipoChave.equals(TipoChave.EMAIL) && (!valorChave.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}\$".toRegex()))) {
            responseObserver.onError(
                Status.INVALID_ARGUMENT
                    .withDescription("formato de email inválido")
                    .asRuntimeException()
            )
            return
        } else {
            if (request.tipoChave.equals(TipoChave.CHAVE_ALEATORIA) && !valorChave.isEmpty()) {
                responseObserver.onError(
                    Status.INVALID_ARGUMENT
                        .withDescription("não deve preencher valor da chave")
                        .asRuntimeException()
                )
                return
            } else if (request.tipoChave.equals(TipoChave.CHAVE_ALEATORIA) && valorChave.isEmpty()) {
                valorChave = UUID.randomUUID().toString()
            }
        }


        val chavePix = ChavePix(
            request.idCliente,
            request.tipoChave,
            valorChave,
            request.tipoConta
        )

        repository.save(chavePix)

        println(chavePix.id!!)
        responseObserver.onNext(PixGrpcResponse.newBuilder().setId(chavePix.id!!).build())
        responseObserver.onCompleted()
    }
}