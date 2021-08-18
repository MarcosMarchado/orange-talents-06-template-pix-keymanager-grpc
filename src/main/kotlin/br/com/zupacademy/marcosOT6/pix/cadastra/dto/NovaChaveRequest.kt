package br.com.zupacademy.marcosOT6.pix.cadastra.dto

import br.com.zupacademy.marcosOT6.pix.cadastra.ChaveEntidade
import br.com.zupacademy.marcosOT6.pix.cadastra.TipoDeChave
import br.com.zupacademy.marcosOT6.pix.cadastra.TipoDeConta
import br.com.zupacademy.marcosOT6.pix.validacao.ChavePix
import io.micronaut.core.annotation.Introspected
import java.util.*

class NovaChaveRequest(
    val valorDaChave: String,
    val codigoDoCliente: String,
    val tipoDeChave: TipoDeChave,
    val tipoDeConta: TipoDeConta
){

    fun toModel(): ChaveEntidade {
        /*Se for selecionado chave aleatória independente do valor
        colocado no JSON para o valorDaChave sempre será gerada uma chave aleatória*/
        return ChaveEntidade(
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