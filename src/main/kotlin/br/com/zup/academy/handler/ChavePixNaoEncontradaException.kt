package br.com.zup.academy.handler

import java.lang.RuntimeException

class ChavePixNaoEncontradaException(message : String) : RuntimeException(message) {
}