package br.com.zup.academy.bancoCentral

class CreatePixKeyRequest(
    val keyType : KeyType,
    val key : String,
    val bankAccount: BankAccount,
    val owner: Owner
)