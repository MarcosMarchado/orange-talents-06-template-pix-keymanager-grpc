package br.com.zupacademy.marcosOT6.pix.detalhar

import br.com.zupacademy.marcosOT6.pix.DetalhesChavePixRequest
import br.com.zupacademy.marcosOT6.pix.DetalhesChavePixResponse
import br.com.zupacademy.marcosOT6.pix.PixServiceDetalhesGrpc
import br.com.zupacademy.marcosOT6.pix.validacao.exception.ObjectNotFoundException
import br.com.zupacademy.marcosOT6.pix.validacao.exception.ValorDesconhecidoException
import io.grpc.Status
import io.grpc.stub.StreamObserver
import javax.inject.Singleton

@Singleton
class DetalhaChaveEndpoint(val detalhaChaveService: DetalhaChaveService) :
    PixServiceDetalhesGrpc.PixServiceDetalhesImplBase() {
    override fun detalharChave(
        request: DetalhesChavePixRequest,
        responseObserver: StreamObserver<DetalhesChavePixResponse>
    ) {

        try {
            responseObserver.onNext(detalhaChaveService.detalha(request))
            responseObserver.onCompleted()
        }catch (exception: Exception){
            val error = when(exception){
                is ObjectNotFoundException -> {
                    Status.NOT_FOUND.augmentDescription(exception.message).asRuntimeException()
                }
                is ValorDesconhecidoException -> {
                    Status.INVALID_ARGUMENT.augmentDescription(exception.message).asRuntimeException()
                }
                else -> Status.INTERNAL.augmentDescription("Erro inesperado.").asRuntimeException()
            }
            responseObserver.onError(error)
        }

    }

}