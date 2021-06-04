package br.com.zup.academy.pix

import io.micronaut.validation.validator.constraints.EmailValidator


enum class TipoDeChave {

    CPF {
        override fun valida(chave: String?): Boolean {
            if (chave.isNullOrBlank())
                return false
            return !chave.matches("^[0-9]{11}\$".toRegex())
        }
    },
    EMAIL {
        override fun valida(chave: String?): Boolean {
            if (chave.isNullOrBlank())
                return false
            return EmailValidator().run {
                initialize(null)
                isValid(chave, null)
            }
        }
    },
    CELULAR {
        override fun valida(chave: String?): Boolean {
            if (chave.isNullOrBlank())
                return false
            return chave.matches("^\\\\+[1-9][0-9]\\\\d{1,14}\\\$".toRegex())
        }
    },
    CHAVE_ALEATORIA {
        override fun valida(chave: String?) = chave.isNullOrBlank()
    };


    abstract fun valida(chave: String?): Boolean
}