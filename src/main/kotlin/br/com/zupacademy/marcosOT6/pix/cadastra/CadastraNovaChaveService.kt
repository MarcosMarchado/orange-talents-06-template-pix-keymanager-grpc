package br.com.zupacademy.marcosOT6.pix.cadastra

import br.com.zupacademy.marcosOT6.pix.cadastra.dto.NovaChaveRequest
import br.com.zupacademy.marcosOT6.pix.cadastra.requisicao.bcb.CadastraChavePixNoBCB
import br.com.zupacademy.marcosOT6.pix.cadastra.requisicao.bcb.CadastraChavePixResponse
import br.com.zupacademy.marcosOT6.pix.cadastra.requisicao.bcb.CadastrarChavePixRequest
import br.com.zupacademy.marcosOT6.pix.cadastra.requisicao.erp.BuscaInformacoesDaConta
import br.com.zupacademy.marcosOT6.pix.cadastra.requisicao.erp.Conta
import br.com.zupacademy.marcosOT6.pix.validacao.exception.ChaveJaExistenteException
import br.com.zupacademy.marcosOT6.pix.validacao.exception.ObjectNotFoundException
import br.com.zupacademy.marcosOT6.pix.validacao.exception.ValorDesconhecidoException
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.exceptions.HttpClientResponseException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.inject.Singleton
import javax.transaction.Transactional

@Singleton
class CadastraNovaChaveService(
    val repository: ChaveRepository,
    val cadastraChavePixNoBCB: CadastraChavePixNoBCB,
    val buscaInformacoesDaConta: BuscaInformacoesDaConta
) {

    val logger: Logger = LoggerFactory.getLogger(this::class.java)

    @Transactional
    fun cadastraChave(novaChaveRequest: NovaChaveRequest): ChaveEntidade {

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
            val contaAssociada: ContaAssociada = conta.body().toModel()
            val chave: ChaveEntidade = novaChaveRequest.toModel(contaAssociada)
            repository.save(chave) /*Passa primeiro pela validação dos dados antes da requisição para o BCB*/

            val response: HttpResponse<CadastraChavePixResponse> = cadastraChavePixNoBCB.cadastra(CadastrarChavePixRequest(chave))
            chave.associaChave(response.body().key) /*Associa a chave de retorno do BCB*/
            return chave
        }catch (exception: HttpClientResponseException){
            when(exception.status){
                HttpStatus.UNPROCESSABLE_ENTITY ->
                    throw ChaveJaExistenteException("A chave ${novaChaveRequest.valorDaChave} já está cadastrada no Banco Central.")
                else -> throw ObjectNotFoundException("Conta não encontrada no sistema Itaú.")
            }
        }

    }


}