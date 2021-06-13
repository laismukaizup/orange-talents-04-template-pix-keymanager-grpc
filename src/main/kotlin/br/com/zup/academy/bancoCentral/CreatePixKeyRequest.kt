package br.com.zup.academy.bancoCentral

data class CreatePixKeyRequest(
    val keyType : String,
    val key : String,
    val bankAccount: BankAccount,
    val owner: Owner
)