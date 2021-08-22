package br.com.zupacademy.marcosOT6.pix.cadastra.dto

import br.com.zupacademy.marcosOT6.pix.cadastra.ChaveEntidade
import br.com.zupacademy.marcosOT6.pix.cadastra.ContaAssociada
import br.com.zupacademy.marcosOT6.pix.cadastra.TipoDeChave
import br.com.zupacademy.marcosOT6.pix.cadastra.TipoDeConta

class NovaChaveRequest(
    val valorDaChave: String,
    val codigoDoCliente: String,
    val tipoDeChave: TipoDeChave,
    val tipoDeConta: TipoDeConta
){

    fun toModel(contaAssociada: ContaAssociada): ChaveEntidade {
        return ChaveEntidade(
            conta = contaAssociada,
            tipoDeConta = tipoDeConta,
            tipoDeChave = tipoDeChave,
            codigoDoCliente = codigoDoCliente,
            valorDaChave = valorDaChave
        )
    }

}