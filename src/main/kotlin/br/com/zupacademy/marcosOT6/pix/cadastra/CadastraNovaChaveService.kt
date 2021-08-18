package br.com.zupacademy.marcosOT6.pix.cadastra

import br.com.zupacademy.marcosOT6.pix.cadastra.dto.NovaChaveRequest
import br.com.zupacademy.marcosOT6.pix.cadastra.requisicao.BuscaInformacoesDaConta
import br.com.zupacademy.marcosOT6.pix.cadastra.requisicao.Conta
import br.com.zupacademy.marcosOT6.pix.validacao.exception.ChaveJaExistenteException
import io.micronaut.http.HttpResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.inject.Singleton

@Singleton
class CadastraNovaChaveService(
    val repository: ChaveRepository,
    val buscaInformacoesDaConta: BuscaInformacoesDaConta
) {

    val logger: Logger = LoggerFactory.getLogger(this::class.java)

    fun cadastraChave(novaChaveRequest: NovaChaveRequest): ChaveEntidade? {
        val conta: HttpResponse<Conta> =
            buscaInformacoesDaConta.busca(novaChaveRequest.codigoDoCliente, novaChaveRequest.tipoDeConta.name)
        if (repository.existsByValorDaChave(novaChaveRequest.valorDaChave)) {
            throw ChaveJaExistenteException("A chave ${novaChaveRequest.valorDaChave} já está cadastrada.")
        }

        return repository.save(novaChaveRequest.toModel())
    }


}