package br.com.zup.academy.pix.cadastra

import br.com.zup.academy.pix.ChavePix
import br.com.zup.academy.pix.ChavePixRepository
import br.com.zup.academy.itau.ItauClient
import io.micronaut.validation.Validated
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.Valid

@Validated
@Singleton
class ChavePixService(
    @Inject val repository: ChavePixRepository,
    @Inject val itauClient: ItauClient
) {

    @Transactional
    fun cadastra(@Valid request: ChavePixRequest): ChavePix {
        println()
        val itauResponse = itauClient.consulta(request.clienteId!!, request.tipoConta!!.name)
        val conta = itauResponse.body()?.toModel() ?: throw IllegalStateException("cliente itaú não encontrado")

        val chavePix = request.toModel(conta);
        repository.save(chavePix)

        return chavePix
    }
}

//    val statusProto = com.google.rpc.Status.newBuilder()
//                .setCode(Code.NOT_FOUND.number)
//                .setMessage("dados de entrada inválido")
//                .addDetails(
//                    Any.pack(
//                        ErrorDetails.newBuilder()
//                            .setCode(404)
//                            .setMessage("id do cliente inválido")
//                            .build()
//                    )
//                )
//                .build()
//            val e = io.grpc.protobuf.StatusProto
//                .toStatusRuntimeException(statusProto)
//            responseObserver.onError(e)
//            return
//        }
//
//
//        if (repository.existsByValorChave(request.valorChave)) {
//            responseObserver.onError(
//                Status.ALREADY_EXISTS
//                    .withDescription("valor da chave já cadastrada")
//                    .asRuntimeException()
//            )
//            return
//        }
//
//        var valorChave = request.valorChave;
//        if (request.tipoChave.equals(TipoDeChave.CPF) && (!valorChave.matches("^[0-9]{11}\$".toRegex()))) {
//            responseObserver.onError(
//                Status.INVALID_ARGUMENT
//                    .withDescription("formato inválido - Ex de formato válido: 12345678901")
//                    .asRuntimeException()
//            )
//            return
//        } else if (request.tipoChave.equals(TipoDeChave.CELULAR) && (!valorChave.matches("^\\+[1-9][0-9]\\d{1,14}\$".toRegex()))) {
//            responseObserver.onError(
//                Status.INVALID_ARGUMENT
//                    .withDescription("formato inválido - Ex de formato válido: +5585988714077")
//                    .asRuntimeException()
//            )
//            return
//        } else if (request.tipoChave.equals(TipoDeChave.EMAIL) && (!valorChave.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}\$".toRegex()))) {
//            responseObserver.onError(
//                Status.INVALID_ARGUMENT
//                    .withDescription("formato de email inválido")
//                    .asRuntimeException()
//            )
//            return
//        } else {
//            if (request.tipoChave.equals(TipoDeChave.CHAVE_ALEATORIA) && !valorChave.isEmpty()) {
//                responseObserver.onError(
//                    Status.INVALID_ARGUMENT
//                        .withDescription("não deve preencher valor da chave")
//                        .asRuntimeException()
//                )
//                return
//            } else if (request.tipoChave.equals(TipoDeChave.CHAVE_ALEATORIA) && valorChave.isEmpty()) {
//                valorChave = UUID.randomUUID().toString()
//            }
//        }
//
