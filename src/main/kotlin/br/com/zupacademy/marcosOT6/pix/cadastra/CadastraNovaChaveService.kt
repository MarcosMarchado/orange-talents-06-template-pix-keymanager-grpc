package br.com.zupacademy.marcosOT6.pix.cadastra

import br.com.zupacademy.marcosOT6.pix.cadastra.dto.NovaChaveRequest
import br.com.zupacademy.marcosOT6.pix.cadastra.requisicao.bcb.*
import br.com.zupacademy.marcosOT6.pix.cadastra.requisicao.erp.BuscaInformacoesDaConta
import br.com.zupacademy.marcosOT6.pix.cadastra.requisicao.erp.Conta
import br.com.zupacademy.marcosOT6.pix.validacao.exception.ChaveJaExistenteException
import br.com.zupacademy.marcosOT6.pix.validacao.exception.ObjectNotFoundException
import br.com.zupacademy.marcosOT6.pix.validacao.exception.ValorDesconhecidoException
import io.micronaut.http.HttpResponse
import io.micronaut.http.client.exceptions.HttpClientResponseException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.inject.Singleton

@Singleton
class CadastraNovaChaveService(
    val repository: ChaveRepository,
    val cadastraChavePixNoBCB: CadastraChavePixNoBCB,
    val buscaInformacoesDaConta: BuscaInformacoesDaConta
) {

    val logger: Logger = LoggerFactory.getLogger(this::class.java)

    fun cadastraChave(novaChaveRequest: NovaChaveRequest): ChaveEntidade? {

        when {
            novaChaveRequest.codigoDoCliente.isBlank() ->
                throw ValorDesconhecidoException("Código do cliente obrigatório.")
            novaChaveRequest.tipoDeChave == TipoDeChave.UNKNOW_TIPO_CHAVE ->
                throw ValorDesconhecidoException("Tipo de Chave desconhecida.")
            novaChaveRequest.tipoDeConta == TipoDeConta.UNKNOW_TIPO_CONTA ->
                throw ValorDesconhecidoException("Tipo de Conta desconhecida.")
            repository.existsByValorDaChave(novaChaveRequest.valorDaChave) ->
                throw ChaveJaExistenteException("A chave ${novaChaveRequest.valorDaChave} já está cadastrada.")
        }

        try {
            val conta: HttpResponse<Conta> = buscaInformacoesDaConta.busca(novaChaveRequest.codigoDoCliente, novaChaveRequest.tipoDeConta.name)
            val contaAssociada = conta.body().toModel()
            val chave: ChaveEntidade = novaChaveRequest.toModel(contaAssociada)

            val retornoBCB = cadastraChavePixNoBCB.cadastra(CadastrarChavePixRequest(chave)).body()
            logger.info("Esse é o retorno do sistema BCB $retornoBCB")

            return repository.save(chave)
        }catch (exception: HttpClientResponseException){
            logger.info("$exception")
            throw ObjectNotFoundException("Conta não encontrada no sistema Itaú.")
        }

    }


}