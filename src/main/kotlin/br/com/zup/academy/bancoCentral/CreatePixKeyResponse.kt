package br.com.zup.academy.bancoCentral

import br.com.zup.academy.pix.carrega.ChavePixInfo
import br.com.zup.academy.pix.carrega.ContaInfo
import br.com.zup.academy.pix.modelo.Conta
import br.com.zup.academy.pix.modelo.Instituicao
import br.com.zup.academy.pix.modelo.Titular

data class CreatePixKeyResponse(
    var keyType: KeyType,
    var key: String,
    var bankAccount: BankAccount,
    var owner: Owner,
    var createdAt: String
) {

    fun toModel(): ChavePixInfo {
        return ChavePixInfo(
            null,
            null,
            keyType.convertToTipoDeChave(),
            key,
            bankAccount.accountType.convertToTipoDeConta(),
            ContaInfo(
                "Itau",
                owner.name,
                owner.taxIdNumber,
                bankAccount.branch,
                bankAccount.accountNumber
            )
        )
    }
}