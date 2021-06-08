package br.com.zup.academy.pix.modelo

import javax.persistence.Embeddable

@Embeddable
class Instituicao(val nome: String, val ispb: String) {

}
