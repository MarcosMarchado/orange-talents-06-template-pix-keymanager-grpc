package br.com.zupacademy.marcosOT6.pix.cadastra.dto

import br.com.zupacademy.marcosOT6.pix.cadastra.ChaveEntidade
import br.com.zupacademy.marcosOT6.pix.cadastra.ContaAssociada
import br.com.zupacademy.marcosOT6.pix.cadastra.TipoDeChave
import br.com.zupacademy.marcosOT6.pix.cadastra.TipoDeConta
import java.util.*

class NovaChaveRequest(
    val valorDaChave: String,
    val codigoDoCliente: String,
    val tipoDeChave: TipoDeChave,
    val tipoDeConta: TipoDeConta
){
    /*TODO: Quando for chave aleatória o valor da chave retornado deve ser salvo
    *  / deve receber um outro parametro que seria a chave de retorno do sistema do Banco Central*/
    fun toModel(contaAssociada: ContaAssociada): ChaveEntidade {
        /*Se for selecionado chave aleatória
        não deve ser passado o campo valorDaChave no JSON*/
        return ChaveEntidade(
            conta = contaAssociada,
            tipoDeConta = tipoDeConta,
            tipoDeChave = tipoDeChave,
            codigoDoCliente = codigoDoCliente,
            valorDaChave = tipoDeChave.run {
                if(name == TipoDeChave.CHAVE_ALEATORIA.name){
                    return@run UUID.randomUUID().toString()
                }
                return@run valorDaChave
            }
        )
    }

}