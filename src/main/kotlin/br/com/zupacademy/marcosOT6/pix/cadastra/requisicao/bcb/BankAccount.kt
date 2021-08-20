package br.com.zupacademy.marcosOT6.pix.cadastra.requisicao.bcb

import br.com.zupacademy.marcosOT6.pix.cadastra.ChaveEntidade

data class BankAccount (
    val participant: String,
    val branch: String,
    val accountNumber: String,
    val accountType: AccounType
){

   companion object{
       fun convert(chave: ChaveEntidade): BankAccount {
           return BankAccount(
               participant = chave.conta.ispb,
               branch = chave.conta.agencia,
               accountNumber = chave.conta.numero,
               accountType = AccounType.convert(chave.tipoDeConta)
           )
       }
   }

}