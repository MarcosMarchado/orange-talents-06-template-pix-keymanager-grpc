package br.com.zupacademy.marcosOT6.pix.detalhar.requisicao.bcb

import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Consumes
import io.micronaut.http.annotation.Get
import io.micronaut.http.client.annotation.Client


@Client(value = "\${http.services.BCB.url}")
interface BuscaDetalhesChavePix {

    @Consumes(MediaType.APPLICATION_XML)
    @Get("{key}")
    fun busca(key: String) : HttpResponse<DetalhesDaChavePixResponseBCB>
}
