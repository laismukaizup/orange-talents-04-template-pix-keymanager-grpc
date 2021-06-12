package br.com.zup.academy.bancoCentral

import br.com.zup.academy.pix.modelo.Conta

class CreatePixKeyResponse(
    val keyType:String,
    val key:String,
    val bankAccount: BankAccount,
    val owner: Owner,
    val createdAt:String
)