package br.com.zupacademy.marcosOT6.pix.remover

import br.com.zupacademy.marcosOT6.pix.cadastra.ChaveRepository
import br.com.zupacademy.marcosOT6.pix.validacao.exception.ObjectNotFoundException
import io.micronaut.validation.Validated
import java.util.*
import javax.inject.Singleton
import javax.validation.constraints.NotBlank

@Validated
@Singleton
class RemoveChaveService(val repository: ChaveRepository) {

    fun remover(
        @NotBlank pixId: String,
        @NotBlank codigoInterno: String
    ){
        val chaveId: UUID = UUID.fromString(pixId)
        val chave = repository
            .findByChaveIdAndCodigoDoCliente(chaveId, codigoInterno)
            .orElseThrow{ ObjectNotFoundException("Chave não encontrada.") }
        repository.delete(chave)
    }

}