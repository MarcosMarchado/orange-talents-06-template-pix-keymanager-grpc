package br.com.zupacademy.marcosOT6.pix.remover

import br.com.zupacademy.marcosOT6.pix.PixServiceRemoverGrpc
import br.com.zupacademy.marcosOT6.pix.RemoverChavePixRequest
import br.com.zupacademy.marcosOT6.pix.RemoverChavePixResponse
import br.com.zupacademy.marcosOT6.pix.validacao.exception.ObjectNotFoundException
import io.grpc.Status
import io.grpc.stub.StreamObserver
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.inject.Singleton
import javax.validation.ConstraintViolationException

@Singleton
class RemoveChaveEndpoint(
    val removeChaveService: RemoveChaveService
) : PixServiceRemoverGrpc.PixServiceRemoverImplBase() {

    val logger: Logger = LoggerFactory.getLogger(this::class.java)

    override fun removerChave(
        request: RemoverChavePixRequest,
        responseObserver: StreamObserver<RemoverChavePixResponse>
    ) {

        try {
            removeChaveService.remover(request.pixId, request.codigoInterno)
            responseObserver.onNext(
                RemoverChavePixResponse
                            .newBuilder()
                            .setCodigoInterno(request.codigoInterno)
                            .setPixId(request.pixId)
                            .build()
            )
            responseObserver.onCompleted()
        }catch (exception: Exception){
            val error = when(exception){
                is ObjectNotFoundException -> Status.NOT_FOUND
                                                .augmentDescription(exception.message)
                                                .asRuntimeException()
                is ConstraintViolationException -> Status.INVALID_ARGUMENT
                                                .augmentDescription(exception.message)
                                                .asRuntimeException()
                else -> Status.INTERNAL.augmentDescription("Erro inesperado").asRuntimeException()
            }
            responseObserver.onError(error)
        }

    }
}