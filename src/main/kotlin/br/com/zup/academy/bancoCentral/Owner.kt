package br.com.zup.academy.bancoCentral

data class Owner(
    val type: OwnerType,
    val name: String,
    val taxIdNumber: String
) {

}