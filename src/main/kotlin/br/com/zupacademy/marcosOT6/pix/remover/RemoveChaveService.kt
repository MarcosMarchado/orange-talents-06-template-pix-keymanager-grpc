package br.com.zupacademy.marcosOT6.pix.remover

import br.com.zupacademy.marcosOT6.pix.cadastra.ChaveEntidade
import br.com.zupacademy.marcosOT6.pix.cadastra.ChaveRepository
import br.com.zupacademy.marcosOT6.pix.remover.requisicao.bcb.RemoveChavePixNoBCB
import br.com.zupacademy.marcosOT6.pix.remover.requisicao.bcb.dto.RemoveChaveRequest
import br.com.zupacademy.marcosOT6.pix.validacao.exception.ObjectNotFoundException
import io.micronaut.http.HttpStatus
import io.micronaut.validation.Validated
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*
import javax.inject.Singleton
import javax.validation.constraints.NotBlank

@Validated
@Singleton
class RemoveChaveService(
    val repository: ChaveRepository,
    val removeChavePixNoBCB: RemoveChavePixNoBCB
) {

    val logger: Logger = LoggerFactory.getLogger(this::class.java)

    fun remover(
        @NotBlank pixId: String,
        @NotBlank codigoInterno: String
    ) {
        val chaveId: UUID = UUID.fromString(pixId)
        val chave: ChaveEntidade = repository
            .findByChaveIdAndCodigoDoCliente(chaveId, codigoInterno)
            .orElseThrow { ObjectNotFoundException("Chave não encontrada.") }

        val request = RemoveChaveRequest(chave.valorDaChave, chave.conta.ispb)

        removeChavePixNoBCB
            .remove(chave.valorDaChave, request)
            .run {
                if (status.equals(HttpStatus.NOT_FOUND))
                    throw ObjectNotFoundException("Chave pix não encontrada no BCB.")
            }

        repository.deleteById(chaveId)

    }

}