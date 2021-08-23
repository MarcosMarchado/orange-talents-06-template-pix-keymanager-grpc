package br.com.zupacademy.marcosOT6.pix.remover.requisicao.bcb

import br.com.zupacademy.marcosOT6.pix.remover.requisicao.bcb.dto.RemoveChaveRequest
import br.com.zupacademy.marcosOT6.pix.remover.requisicao.bcb.dto.RemoveChaveResponse
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.http.client.annotation.Client

@Client(value = "\${http.services.BCB.url}")
interface RemoveChavePixNoBCB {

    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    @Delete(value = "{key}")
    fun remove(@PathVariable key: String, @Body request: RemoveChaveRequest) : HttpResponse<RemoveChaveResponse>

}