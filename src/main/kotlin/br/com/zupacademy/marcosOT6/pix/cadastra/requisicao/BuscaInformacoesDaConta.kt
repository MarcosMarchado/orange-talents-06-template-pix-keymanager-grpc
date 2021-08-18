package br.com.zupacademy.marcosOT6.pix.cadastra.requisicao

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.client.annotation.Client

@Client(value = "\${http.services.ERP.url}")
interface BuscaInformacoesDaConta {
   @Get(value = "clientes/{clienteId}/contas")
   fun busca(@PathVariable clienteId: String, @QueryValue tipo: String) : HttpResponse<Conta>
}