package br.com.zup.academy.bancoCentral

data class CreatePixKeyResponse(
    var keyType: KeyType,
    var key: String,
    var bankAccount: BankAccount,
    var owner: Owner,
    var createdAt: String
)