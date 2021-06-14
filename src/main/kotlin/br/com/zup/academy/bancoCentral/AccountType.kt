package br.com.zup.academy.bancoCentral

import br.com.zup.academy.pix.modelo.TipoDeConta


enum class AccountType {
    CACC, SVGS;

    fun convertToTipoDeConta(): TipoDeConta {
        return when (this) {
            AccountType.CACC -> TipoDeConta.CONTA_CORRENTE
            AccountType.SVGS -> TipoDeConta.CONTA_POUPANCA
        }
    }
}

