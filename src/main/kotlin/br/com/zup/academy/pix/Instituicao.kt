package br.com.zup.academy.pix

import javax.persistence.Embeddable

@Embeddable
class Instituicao(val nome: String, val ispb: String) {

}
