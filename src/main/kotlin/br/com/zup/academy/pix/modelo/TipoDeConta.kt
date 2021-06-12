package br.com.zup.academy.pix.modelo

import br.com.zup.academy.bancoCentral.AccountType


enum class TipoDeConta {
    CONTA_CORRENTE, CONTA_POUPANCA;

    fun converter(): AccountType {
        return when (this) {
            CONTA_CORRENTE -> AccountType.CACC
            CONTA_POUPANCA -> AccountType.SVGS
        }
    }
}