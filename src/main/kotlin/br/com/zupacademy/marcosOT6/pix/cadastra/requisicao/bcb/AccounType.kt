package br.com.zupacademy.marcosOT6.pix.cadastra.requisicao.bcb

import br.com.zupacademy.marcosOT6.pix.cadastra.TipoDeChave
import br.com.zupacademy.marcosOT6.pix.cadastra.TipoDeConta

enum class AccounType {
    CACC, /*Conta corrente*/
    SVGS; /*Conta Poupança*/


    companion object {
        fun convert(tipoDeConta: TipoDeConta): AccounType {
            val accounType = when (tipoDeConta) {
                TipoDeConta.CONTA_CORRENTE -> CACC
                else -> SVGS
            }
            return accounType
        }
    }


}