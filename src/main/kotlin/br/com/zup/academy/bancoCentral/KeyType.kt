package br.com.zup.academy.bancoCentral

import br.com.zup.academy.pix.modelo.TipoDeChave

enum class KeyType {
    CPF, PHONE, EMAIL, RANDOM;

    fun convertToTipoDeChave(): TipoDeChave {
        return when (this) {
            CPF -> TipoDeChave.CPF
            EMAIL -> TipoDeChave.EMAIL
            PHONE -> TipoDeChave.CELULAR
            RANDOM -> TipoDeChave.CHAVE_ALEATORIA
        }
    }
}