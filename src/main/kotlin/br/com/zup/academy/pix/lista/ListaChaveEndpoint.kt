package br.com.zup.academy.pix.lista

import br.com.zup.academy.ListaChavePixGRPCServiceGrpc
import br.com.zup.academy.ListaChavePixRequest
import br.com.zup.academy.ListaChavePixResponse
import br.com.zup.academy.handler.ErrorHandler
import br.com.zup.academy.pix.ChavePixService
import br.com.zup.academy.pix.cadastra.toModel
import com.google.protobuf.Timestamp
import io.grpc.stub.StreamObserver
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ErrorHandler
class ListaChaveEndpoint(@Inject private val chavePixService: ChavePixService) :
    ListaChavePixGRPCServiceGrpc.ListaChavePixGRPCServiceImplBase() {

    override fun listar(request: ListaChavePixRequest, responseObserver: StreamObserver<ListaChavePixResponse>) {
        if (request.clienteId.isNullOrBlank())
            throw IllegalArgumentException("id do cliente não pode ser nula")

        val requestCP = request.toModel();
        val chaves = chavePixService.lista(requestCP).map {
            ListaChavePixResponse.ChavePix.newBuilder()
                .setPixId(it.id)
                .setTipoChave(it.tipoChave!!.converterToGRPCObject())
                .setValorChave(it.valorChave)
                .setTipoConta(it.tipoConta!!.converterToGrpcObject())
                .setCriadoEm(it.criadaEm.let {
                    val createAt = it.atZone(ZoneId.of("UTC")).toInstant()
                    Timestamp.newBuilder().setSeconds(createAt.epochSecond)
                        .setNanos(createAt.nano).build()
                })
                .build()
        }

        responseObserver.onNext(
            ListaChavePixResponse.newBuilder()
                .setClienteId(requestCP.clienteId)
                .addAllChaves(chaves)
                .build()
        )

        responseObserver.onCompleted()
    }

}