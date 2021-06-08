package br.com.zup.academy.pix

import br.com.zup.academy.itau.ItauClient
import br.com.zup.academy.pix.cadastra.CadastraCPRequest
import br.com.zup.academy.pix.modelo.ChavePix
import br.com.zup.academy.pix.remove.RemoveCPRequest
import io.grpc.Status
import io.grpc.StatusRuntimeException
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
    fun cadastra(@Valid request: CadastraCPRequest): ChavePix {
        val itauResponse = itauClient.consulta(request.clienteId.toString(), request.tipoConta!!.name)
        val conta = itauResponse.body()?.toModel() ?: throw IllegalStateException("cliente itau não encontrado")
        val chavePix = request.toModel(conta);
        repository.save(chavePix)

        return chavePix
    }

    fun remove(@Valid request: RemoveCPRequest): Boolean {
        val itauResponse = itauClient.consulta(request.clienteId.toString())
        itauResponse.body()?.toModel() ?: throw IllegalStateException("cliente itau não encontrado")
        //verifica se existe uma chave para o cliente/idpix passado como parametro
        val findByIdClienteAndPixId = repository.find(
            request.clienteId,
            request.pixId
        )
        if (findByIdClienteAndPixId.isEmpty) {
            throw StatusRuntimeException(Status.NOT_FOUND.withDescription("chave não encontrada"))
        }
        repository.deleteById(request.pixId)
        return true
    }
}

//
//val teste = repository.findAll()
//println("ClienteId :  ${teste.get(1).idCliente} - PixId : ${teste.get(1).pixId} ")
//val findById = repository.findByPixId(teste.get(1).pixId!!)
//println("id está presente: " +findById.isPresent)