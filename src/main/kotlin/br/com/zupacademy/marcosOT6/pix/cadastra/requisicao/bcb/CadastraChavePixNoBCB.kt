package br.com.zupacademy.marcosOT6.pix.cadastra.requisicao.bcb

import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Post
import io.micronaut.http.client.annotation.Client

@Client(value = "\${http.services.BCB.url}")
interface CadastraChavePixNoBCB {
    /*Só vai cadastrar a chave no nosso sistema quando der Ok no BCB*/
    @Post(produces = [MediaType.APPLICATION_XML], consumes = [MediaType.APPLICATION_XML])
    fun cadastra(@Body request: CadastrarChavePixRequestBCB) : HttpResponse<CadastraChavePixResponseBCB>
}