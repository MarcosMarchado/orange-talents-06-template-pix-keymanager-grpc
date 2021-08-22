package br.com.zupacademy.marcosOT6.pix.cadastra

import br.com.zupacademy.marcosOT6.pix.CadastraChavePixRequest
import br.com.zupacademy.marcosOT6.pix.CadastraChavePixResponse
import br.com.zupacademy.marcosOT6.pix.PixServiceGrpc
import br.com.zupacademy.marcosOT6.pix.cadastra.dto.NovaChaveRequest
import br.com.zupacademy.marcosOT6.pix.validacao.exception.ChaveJaExistenteException
import br.com.zupacademy.marcosOT6.pix.validacao.exception.ObjectNotFoundException
import br.com.zupacademy.marcosOT6.pix.validacao.exception.ValorDesconhecidoException
import io.grpc.Status
import io.grpc.stub.StreamObserver
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.inject.Singleton
import javax.validation.ConstraintViolationException

@Singleton
class CadastraNovaChaveEndpoint(
    val cadastraNovaChaveService: CadastraNovaChaveService
) : PixServiceGrpc.PixServiceImplBase(){

    val logger: Logger = LoggerFactory.getLogger(this::class.java)

    override fun cadastrarChave(
        request: CadastraChavePixRequest,
        responseObserver: StreamObserver<CadastraChavePixResponse>
    ) {
        /*Converte para um modelo interno para facilitar a manipulação dos dados*/
        val chaveRequest: NovaChaveRequest = request.toModel()

        try {
            val novaChave: ChaveEntidade = cadastraNovaChaveService.cadastraChave(chaveRequest)
            responseObserver.onNext(CadastraChavePixResponse
                                .newBuilder()
                                .setPixId(novaChave.chaveId.toString())
                                .build())
            responseObserver.onCompleted()
        }catch (exception: Exception){
            when(exception){
                is ConstraintViolationException, is ValorDesconhecidoException -> {
                    val error = Status.INVALID_ARGUMENT
                                .augmentDescription(exception.message)
                                .asRuntimeException()
                    responseObserver.onError(error)
                }
                is ChaveJaExistenteException -> {
                    val error = Status.ALREADY_EXISTS
                                .augmentDescription(exception.message)
                                .asRuntimeException()
                    responseObserver.onError(error)
                }
                is ObjectNotFoundException -> {
                    val error = Status.NOT_FOUND
                                .augmentDescription(exception.message)
                                .asRuntimeException()
                    responseObserver.onError(error)
                }
            }
        }

    }

}