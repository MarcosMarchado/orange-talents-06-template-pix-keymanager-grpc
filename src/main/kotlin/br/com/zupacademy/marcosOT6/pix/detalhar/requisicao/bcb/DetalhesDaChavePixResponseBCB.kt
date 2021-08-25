package br.com.zupacademy.marcosOT6.pix.detalhar.requisicao.bcb

import br.com.zupacademy.marcosOT6.pix.cadastra.ChaveEntidade
import br.com.zupacademy.marcosOT6.pix.cadastra.ContaAssociada
import br.com.zupacademy.marcosOT6.pix.cadastra.TipoDeChave
import br.com.zupacademy.marcosOT6.pix.cadastra.TipoDeConta
import java.time.LocalDateTime

data class DetalhesDaChavePixResponseBCB (
    val keyType: String,
    val key: String,
    val bankAccount: BankAccount,
    val owner: Owner,
    val createdAt: String
){
    fun toModel(): ChaveEntidade {

        val tipoDeConta: TipoDeConta = bankAccount.accountType.let {
            when(it){
                "CACC" -> TipoDeConta.CONTA_CORRENTE
                else -> TipoDeConta.CONTA_POUPANCA
            }
        }
        val tipoDeChave: TipoDeChave = keyType.let {
            when(it){
                "CPF" -> TipoDeChave.CPF
                "PHONE" -> TipoDeChave.TELEFONE
                "EMAIL" -> TipoDeChave.EMAIL
                else -> TipoDeChave.CHAVE_ALEATORIA
            }
        }

        val chave = ChaveEntidade(
            codigoDoCliente = "",
            valorDaChave = key,
            tipoDeConta = tipoDeConta,
            tipoDeChave = tipoDeChave,
            conta = ContaAssociada(
                nomeDaInstituicao = "Itau", //TODO: Falta Refatorar
                ispb = bankAccount.participant,
                agencia = bankAccount.branch,
                numero = bankAccount.accountNumber,
                nomeDoTitular = owner.name,
                cpfDoTitular = owner.taxIdNumber
            )
        )
        chave.criadoEm = LocalDateTime.parse(createdAt)
        return chave
    }
}

