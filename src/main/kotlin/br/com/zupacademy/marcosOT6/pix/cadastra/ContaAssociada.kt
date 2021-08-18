package br.com.zupacademy.marcosOT6.pix.cadastra

import javax.persistence.Embeddable

@Embeddable
data class ContaAssociada(
    val nomeDaInstituicao: String,
    val ispb: String,
    val agencia: String,
    val numero: String,
    val nomeDoTitular: String,
    val cpfDoTitular: String
)
