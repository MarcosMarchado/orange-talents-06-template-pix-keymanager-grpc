package br.com.zupacademy.marcosOT6.pix.listar

import br.com.zupacademy.marcosOT6.pix.ListarChavesPixRequest
import br.com.zupacademy.marcosOT6.pix.ListarChavesPixResponse
import br.com.zupacademy.marcosOT6.pix.PixServiceListagemGrpc
import br.com.zupacademy.marcosOT6.pix.validacao.exception.ValorDesconhecidoException
import io.grpc.Status
import io.grpc.stub.StreamObserver
import javax.inject.Singleton

@Singleton
class ListaChavesEndpoint(val listaChavesService: ListaChavesService) :
    PixServiceListagemGrpc.PixServiceListagemImplBase() {

    override fun listarChaves(
        request: ListarChavesPixRequest,
        responseObserver: StreamObserver<ListarChavesPixResponse>
    ) {
        try {
            responseObserver.onNext(listaChavesService.lista(request))
            responseObserver.onCompleted()
        } catch (exception: ValorDesconhecidoException) {
            val error = Status.INVALID_ARGUMENT
                                .augmentDescription(exception.message)
                                .asRuntimeException()
            responseObserver.onError(error)
        }
    }

}