package br.com.zupacademy.marcosOT6.pix.detalhar

import br.com.zupacademy.marcosOT6.pix.*
import br.com.zupacademy.marcosOT6.pix.cadastra.ChaveEntidade
import br.com.zupacademy.marcosOT6.pix.cadastra.ChaveRepository
import br.com.zupacademy.marcosOT6.pix.detalhar.requisicao.bcb.BuscaDetalhesChavePix
import br.com.zupacademy.marcosOT6.pix.validacao.exception.ValorDesconhecidoException
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

            /*request.keyManager.pixId.isNullOrBlank() || request.keyManager.codigoInterno.isNullOrBlank() ->
                throw ValorDesconhecidoException("Valores obrigatórios.")*/
        }

        //Para busca no KeyManager
        if(!request.keyManager.pixId.isNullOrBlank() && !request.keyManager.codigoInterno.isNullOrBlank()){
            val chaveId: UUID = try {
                UUID.fromString(request.keyManager.pixId)
            }catch (exception: IllegalArgumentException) {
                throw ValorDesconhecidoException("O valor PixId não atende a um formato válido.")
            }

            val idClient = request.keyManager.codigoInterno
            val possivelChave: Optional<ChaveEntidade> = repository.findByChaveIdAndCodigoDoCliente(chaveId, idClient)

            //Se o valor do Optional for vazio, deve buscar lá na API


            val conta = Conta
                .newBuilder()
                .setTipoDeConta(TipoDeConta.valueOf(possivelChave.get().tipoDeConta.name))
                .setAgencia(possivelChave.get().conta.agencia)
                .setNumero(possivelChave.get().conta.numero)
                .setNomeDaInstituicao(possivelChave.get().conta.nomeDaInstituicao)
                .setCPF(possivelChave.get().conta.cpfDoTitular)
                .setTitular(possivelChave.get().conta.nomeDoTitular)
                .build()


            return DetalhesChavePixResponse
                .newBuilder()
                .setPixId(chaveId.toString())
                .setCodigoInterno(idClient)
                .setConta(conta)
                .setTipoDeChave(TipoDeChave.valueOf(possivelChave.get().tipoDeChave.name))
                .setValorDaChave(possivelChave.get().valorDaChave)
                .setDataCriacao(possivelChave.get().criadoEm.toString())
                .build()

        }

        //Para API externas
        //TODO: Falta tratamento de erros
        val chave: ChaveEntidade = repository.findByValorDaChave(request.valorDaChave)
            .run {
                if (isEmpty) {
                    val detalhesChavePix = buscaDetalhesChavePix.busca(request.valorDaChave)
                    return@run detalhesChavePix.body().toModel()
                }
                return@run get()
            }

        val conta = Conta
            .newBuilder()
            .setTitular(chave.conta.nomeDoTitular)
            .setCPF(chave.conta.cpfDoTitular)
            .setNomeDaInstituicao(chave.conta.nomeDaInstituicao)
            .setAgencia(chave.conta.agencia)
            .setNumero(chave.conta.numero)
            .setTipoDeConta(TipoDeConta.valueOf(chave.tipoDeConta.name))
            .build()

        return DetalhesChavePixResponse
            .newBuilder()
            .setPixId("")
            .setCodigoInterno("")
            .setTipoDeChave(TipoDeChave.valueOf(chave.tipoDeChave.name))
            .setValorDaChave(chave.valorDaChave)
            .setConta(conta)
            .setDataCriacao(chave.criadoEm.toString())
            .build()

    }

}