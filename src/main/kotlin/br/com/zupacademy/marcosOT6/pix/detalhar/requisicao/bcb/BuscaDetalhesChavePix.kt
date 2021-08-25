package br.com.zupacademy.marcosOT6.pix.detalhar.requisicao.bcb

import io.micronaut.http.annotation.Get
import io.micronaut.http.client.annotation.Client
import java.net.http.HttpResponse

@Client(value = "\${http.services.BCB.url}")
interface BuscaDetalhesChavePix {
    @Get("{key}")
    fun busca(key: String) : HttpResponse<DetalhesDaChavePixResponseBCB>
}
