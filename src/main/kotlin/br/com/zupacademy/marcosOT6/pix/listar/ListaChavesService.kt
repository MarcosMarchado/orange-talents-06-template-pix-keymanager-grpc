package br.com.zupacademy.marcosOT6.pix.listar

import br.com.zupacademy.marcosOT6.pix.*
import br.com.zupacademy.marcosOT6.pix.cadastra.ChaveEntidade
import br.com.zupacademy.marcosOT6.pix.cadastra.ChaveRepository
import br.com.zupacademy.marcosOT6.pix.validacao.exception.ValorDesconhecidoException
import javax.inject.Singleton

@Singleton
class ListaChavesService(val repository: ChaveRepository) {

    fun lista(request: ListarChavesPixRequest): ListarChavesPixResponse {
        when {
            request.codigoInterno.isNullOrBlank() -> throw ValorDesconhecidoException("Código do cliente obrigatório.")
        }

        val chaves: List<ChaveEntidade> = repository.findByCodigoDoCliente(request.codigoInterno)
        val chavesResponse: List<Chave> = chaves.map { chave ->
            Chave
                .newBuilder()
                .setPixId(chave.chaveId.toString())
                .setCodigoInterno(chave.codigoDoCliente)
                .setTipoDeChave(TipoDeChave.valueOf(chave.tipoDeChave.name))
                .setValorDaChave(chave.valorDaChave)
                .setTipoDeConta(TipoDeConta.valueOf(chave.tipoDeConta.name))
                .setDataCriacao(chave.criadoEm.toString())
                .build()
        }

        return ListarChavesPixResponse.newBuilder().addAllChaves(chavesResponse).build()

    }

}
