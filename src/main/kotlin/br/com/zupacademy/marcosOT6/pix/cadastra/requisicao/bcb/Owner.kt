package br.com.zupacademy.marcosOT6.pix.cadastra.requisicao.bcb

import br.com.zupacademy.marcosOT6.pix.cadastra.ContaAssociada

data class Owner (
    val type: Type, /*Enum*/
    val name: String,
    val taxIdNumber: String
){
    companion object{
        fun convert(conta: ContaAssociada): Owner {
           return Owner(
               type = Type.LEGAL_PERSON,
               name = conta.nomeDoTitular,
               taxIdNumber = conta.cpfDoTitular
           )
        }
    }
}