package br.com.zupacademy.marcosOT6.pix.cadastra.requisicao.bcb

import br.com.zupacademy.marcosOT6.pix.cadastra.ChaveEntidade


data class CadastrarChavePixRequestBCB (
    val chave: ChaveEntidade
){
    val key: String = chave.valorDaChave
    val keyType: KeyType = KeyType.convert(chave.tipoDeChave) /*Enum*/
    val bankAccount: BankAccount = BankAccount.convert(chave)
    val owner: Owner = Owner.convert(chave.conta)
}

