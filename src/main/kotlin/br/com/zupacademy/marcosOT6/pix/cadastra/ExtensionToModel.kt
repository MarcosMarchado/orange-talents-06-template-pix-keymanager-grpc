package br.com.zupacademy.marcosOT6.pix.cadastra

import br.com.zupacademy.marcosOT6.pix.CadastraChavePixRequest
import br.com.zupacademy.marcosOT6.pix.cadastra.dto.NovaChaveRequest

fun CadastraChavePixRequest.toModel(): NovaChaveRequest {
    return NovaChaveRequest(
        tipoDeConta = TipoDeConta.valueOf(tipoDeConta!!.name),
        tipoDeChave = TipoDeChave.valueOf(tipoDeChave.name),
        valorDaChave = valorDaChave,
        codigoDoCliente = codigoInterno
    )
}
