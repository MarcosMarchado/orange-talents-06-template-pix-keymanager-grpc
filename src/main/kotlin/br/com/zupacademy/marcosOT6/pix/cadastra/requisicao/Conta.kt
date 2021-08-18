package br.com.zupacademy.marcosOT6.pix.cadastra.requisicao


data class Conta (
    val instituicao: Instituicao,
    val agencia: String,
    val numero: String,
    val titular: Titular
)