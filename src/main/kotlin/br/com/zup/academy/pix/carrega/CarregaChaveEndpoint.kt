package br.com.zup.academy.pix.carrega

import br.com.zup.academy.*
import br.com.zup.academy.bancoCentral.BCBClient
import br.com.zup.academy.handler.ErrorHandler
import br.com.zup.academy.pix.ChavePixRepository
import br.com.zup.academy.pix.cadastra.toModel
import com.google.protobuf.Timestamp
import io.grpc.stub.StreamObserver
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton
import javax.validation.Validator

@ErrorHandler
@Singleton
class CarregaChaveEndpoint(
    @Inject private val repository: ChavePixRepository,
    @Inject private val bcbClient: BCBClient,
    @Inject private val validator: Validator
) : CarregaChavePixGRPCServiceGrpc.CarregaChavePixGRPCServiceImplBase(){

    override fun carregar(
        request: CarregaChavePixRequest,
        responseObserver: StreamObserver<CarregaChavePixResponse>
    ) {
        val filtro = request.toModel(validator);
        val chaveInfo = filtro.filtra(repository,bcbClient)

        responseObserver.onNext(CarregachavePixResponseConverter().convert(chaveInfo))
        responseObserver.onCompleted()
    }

}

class CarregachavePixResponseConverter(){

    fun convert(chaveInfo: ChavePixInfo) : CarregaChavePixResponse {
        return CarregaChavePixResponse.newBuilder()
            .setClienteId(chaveInfo.clienteId?.toString() ?: "")
            .setPidId(chaveInfo.pixId?.toString() ?: "")
            .setChave(
                CarregaChavePixResponse.ChavePix.newBuilder()
                    .setTipo(TipoDeChaveGRPC.valueOf(chaveInfo.tipoDeChave.name))
                    .setValorChave(chaveInfo.valorChave)
                    .setConta(
                        CarregaChavePixResponse.ChavePix.ContaInfo.newBuilder()
                            .setTipo(TipoDeContaGRPC.valueOf(chaveInfo.tipoDeConta.name))
                            .setInstituicao(chaveInfo.conta.instituicao)
                            .setNomeDoTitular(chaveInfo.conta.nomeDoTitular)
                            .setCpfDoTitular(chaveInfo.conta.cpfDoTitular)
                            .setAgencia(chaveInfo.conta.agencia)
                            .setNumeroDaConta(chaveInfo.conta.numeroDaConta)
                            .build()
                    )
                    .setCriadoEm(chaveInfo.registradaEm.let {
                        val createdAt = it.atZone(ZoneId.of("UTC")).toInstant()
                        Timestamp.newBuilder()
                            .setSeconds(createdAt.epochSecond)
                            .setNanos(createdAt.nano)
                            .build()
                    })
            )
            .build()
    }
}
