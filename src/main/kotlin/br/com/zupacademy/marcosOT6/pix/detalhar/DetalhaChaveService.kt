package br.com.zupacademy.marcosOT6.pix.detalhar

import br.com.zupacademy.marcosOT6.pix.*
import br.com.zupacademy.marcosOT6.pix.cadastra.ChaveEntidade
import br.com.zupacademy.marcosOT6.pix.cadastra.ChaveRepository
import br.com.zupacademy.marcosOT6.pix.detalhar.requisicao.bcb.BuscaDetalhesChavePix
import br.com.zupacademy.marcosOT6.pix.validacao.exception.ObjectNotFoundException
import br.com.zupacademy.marcosOT6.pix.validacao.exception.ValorDesconhecidoException
import io.micronaut.http.HttpStatus
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*
import javax.inject.Singleton

@Singleton
class DetalhaChaveService(
    val repository: ChaveRepository,
    val buscaDetalhesChavePix: BuscaDetalhesChavePix
) {

    val LOGGER: Logger = LoggerFactory.getLogger(this::class.java)

    fun detalha(request: DetalhesChavePixRequest): DetalhesChavePixResponse {

        when {
            !request.hasKeyManager() && request.valorDaChave.isNullOrBlank() ->
                throw ValorDesconhecidoException("Pelo menos uma das formas de busca é obrigatória.")
        }

        val resultado = when (request.hasKeyManager()) {
            true -> buscaNoKeyManager(request) /*Busca chave no banco de dados usando código do cliente e id da chave*/
            else -> buscaPorValorDaChave(request) /*Busca chave usando apenas o valor da chave*/
        }

        val conta = Conta
            .newBuilder()
            .setTipoDeConta(TipoDeConta.valueOf(resultado.tipoDeConta.name))
            .setNomeDaInstituicao(resultado.conta.nomeDaInstituicao)
            .setTitular(resultado.conta.nomeDoTitular)
            .setCPF(resultado.conta.cpfDoTitular)
            .setAgencia(resultado.conta.agencia)
            .setNumero(resultado.conta.numero)
            .build()

        return DetalhesChavePixResponse
            .newBuilder()
            .setTipoDeChave(TipoDeChave.valueOf(resultado.tipoDeChave.name))
            .setCodigoInterno(resultado.codigoDoCliente)
            .setValorDaChave(resultado.valorDaChave)
            .setPixId(if (resultado.chaveId == null) "" else resultado.chaveId.toString())
            .setConta(conta)
            .setDataCriacao(resultado.criadoEm.toString())
            .build()
    }

    private fun buscaNoKeyManager(request: DetalhesChavePixRequest): ChaveEntidade {

        when{
            request.keyManager.codigoInterno.isNullOrBlank() ->
                throw ValorDesconhecidoException("Código do cliente obrigatório.")
            request.keyManager.pixId.isNullOrBlank() ->
                throw ValorDesconhecidoException("Identificador da chave obrigatório.")
        }

        val chaveId: UUID = try {
            UUID.fromString(request.keyManager.pixId)
        } catch (exception: IllegalArgumentException) {
            throw ValorDesconhecidoException("O valor PixId não atende a um formato válido.")
        }
        val idClient = request.keyManager.codigoInterno
        return repository.findByChaveIdAndCodigoDoCliente(chaveId, idClient).orElseThrow {
            ObjectNotFoundException("Chave não encontrada.")
        }
    }

    private fun buscaPorValorDaChave(request: DetalhesChavePixRequest): ChaveEntidade {
        val possivelChave = repository.findByValorDaChave(request.valorDaChave)
        if (possivelChave.isEmpty) {
            return buscaDetalhesChavePix.busca(request.valorDaChave)
                .run {
                    if (status.equals(HttpStatus.NOT_FOUND)) throw ObjectNotFoundException("Chave não encontrada no BCB.")
                    body().toModel()
                }
        }
        return possivelChave.get()
    }

}