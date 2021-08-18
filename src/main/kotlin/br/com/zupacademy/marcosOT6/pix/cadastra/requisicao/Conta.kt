package br.com.zupacademy.marcosOT6.pix.cadastra.requisicao

import br.com.zupacademy.marcosOT6.pix.cadastra.ContaAssociada

data class Conta (
    val instituicao: Instituicao,
    val agencia: String,
    val numero: String,
    val titular: Titular
){
    fun toModel(): ContaAssociada {
        return ContaAssociada(
            nomeDaInstituicao = instituicao.nome,
            ispb = instituicao.ispb,
            agencia = agencia,
            numero = numero,
            nomeDoTitular = titular.nome,
            cpfDoTitular = titular.cpf
        )
    }
}