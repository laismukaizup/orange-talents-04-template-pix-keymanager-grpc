package br.com.zup.academy.pix

import br.com.zup.academy.bancoCentral.BCBClient
import br.com.zup.academy.bancoCentral.DeletePixKeyRequest
import br.com.zup.academy.handler.ChavePixExisteException
import br.com.zup.academy.handler.ChavePixNaoEncontradaException
import br.com.zup.academy.itau.ItauClient
import br.com.zup.academy.pix.cadastra.CadastraCPRequest
import br.com.zup.academy.pix.lista.ListaCPRequest
import br.com.zup.academy.pix.modelo.ChavePix
import br.com.zup.academy.pix.modelo.TipoDeConta
import br.com.zup.academy.pix.remove.RemoveCPRequest
import io.micronaut.http.HttpStatus
import io.micronaut.validation.Validated
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.Valid

@Validated
@Singleton
class ChavePixService(
    @Inject val repository: ChavePixRepository,
    @Inject val itauClient: ItauClient,
    @Inject val bcbClient: BCBClient
) {

    @Transactional
    fun cadastra(@Valid request: CadastraCPRequest): ChavePix {

        if (repository.existsByValorChave(request.valorChave!!))
            throw ChavePixExisteException("Chave pix já cadastrada")

        val itauResponse = itauClient.consulta(request.clienteId.toString(), request.tipoConta!!.name)
        val conta = itauResponse.body()?.toModel() ?: throw IllegalStateException("cliente itau não encontrado")

        val createPixKeyRequest = request.toCreatePixKeyRequest(conta)
        val bcbResponse = bcbClient.enviaRegistro(createPixKeyRequest)
        if (bcbResponse.status != HttpStatus.CREATED)
            throw IllegalStateException("erro ao registrar chave no BCB")
        request.validaValorChave(bcbResponse.body()!!);

        val chavePix = request.toModel(conta);
        repository.save(chavePix)

        return chavePix
    }

    @Transactional
    fun remove(@Valid request: RemoveCPRequest): Boolean {

        val itauResponse = itauClient.consulta(request.clienteId)
        itauResponse.body()?.toModel() ?: throw IllegalStateException("cliente itau não encontrado")

        val possivelChavePix = repository.find(request.clienteId, request.pixId)
        if (possivelChavePix.isEmpty)
            throw ChavePixNaoEncontradaException("chave não encontrada no banco de dados")

        val chavePix = possivelChavePix.get()

        val deletePixKeyRequest = DeletePixKeyRequest(chavePix.valorChave)

        val bcbResponse = bcbClient.deletaChave(chavePix.valorChave, deletePixKeyRequest)
        if (bcbResponse.status != HttpStatus.OK)
            throw IllegalStateException("erro ao deletar chave no BCB")

        repository.deleteById(request.pixId)
        return true
    }

    fun lista(@Valid request: ListaCPRequest): List<ChavePix> {
        return repository.findByIdCliente(request.clienteId)
    }
}