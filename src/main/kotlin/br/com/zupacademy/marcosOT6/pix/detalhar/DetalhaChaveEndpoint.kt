package br.com.zupacademy.marcosOT6.pix.detalhar

import br.com.zupacademy.marcosOT6.pix.DetalhesChavePixRequest
import br.com.zupacademy.marcosOT6.pix.DetalhesChavePixResponse
import br.com.zupacademy.marcosOT6.pix.PixServiceDetalhesGrpc
import io.grpc.stub.StreamObserver
import javax.inject.Singleton

@Singleton
class DetalhaChaveEndpoint(val detalhaChaveService: DetalhaChaveService) : PixServiceDetalhesGrpc.PixServiceDetalhesImplBase() {
    override fun detalharChave(
        request: DetalhesChavePixRequest,
        responseObserver: StreamObserver<DetalhesChavePixResponse>
    ) {

        responseObserver.onNext(detalhaChaveService.detalha(request))
        responseObserver.onCompleted()

    }

}