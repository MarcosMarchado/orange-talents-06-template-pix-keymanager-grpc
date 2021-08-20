package br.com.zupacademy.marcosOT6.pix.remover

import br.com.zupacademy.marcosOT6.pix.PixServiceRemoverGrpc
import br.com.zupacademy.marcosOT6.pix.RemoverChavePixRequest
import br.com.zupacademy.marcosOT6.pix.cadastra.*
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.Assert.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest(transactional = false)
internal class RemoveChaveEndpointTest() {

    @Inject
    lateinit var removeChaveClient: PixServiceRemoverGrpc.PixServiceRemoverBlockingStub

    @Inject
    lateinit var repository: ChaveRepository

    @BeforeEach
    fun setup(){
        repository.deleteAll()
    }

    @Test
    fun `Deve remover uma chave pix existente`(){
        //TODO: Montar cenário
        val codigoDoCliente = "c56dfef4-7901-44fb-84e2-a2cefb157890"
        val conta = ChaveEntidade(
            valorDaChave = "marcos@gmail.com",
            codigoDoCliente = codigoDoCliente,
            tipoDeChave = TipoDeChave.EMAIL,
            tipoDeConta = TipoDeConta.CONTA_CORRENTE,
            ContaAssociada(
                nomeDaInstituicao = "ITAÚ UNIBANCO S.A.",
                ispb = "60701190",
                agencia = "0001",
                numero = "291900",
                nomeDoTitular = "Marcos Machado",
                cpfDoTitular = "05477588873"
            )
        )

        val contaSalva = repository.save(conta)

        val request = RemoverChavePixRequest.newBuilder()
                            .setCodigoInterno(codigoDoCliente)
                            .setPixId(contaSalva.chaveId.toString())
                            .build()

        val response = removeChaveClient.removerChave(request)

        with(response){
            assertEquals(contaSalva.chaveId.toString(), pixId)
            assertEquals(contaSalva.codigoDoCliente, codigoInterno)
        }
    }

    @Test
    fun `Deve dar erro ao remover uma chave que nao exista`(){

        val request = RemoverChavePixRequest.newBuilder()
                            .setPixId("c56dfef4-7901-44fb-84e2-a2cefb157890")
                            .setCodigoInterno("44fb-84e2-a2cefb157890")
                            .build()

        val error = assertThrows<StatusRuntimeException> {
            removeChaveClient.removerChave(request)
        }

        with(error){
            assertEquals(Status.NOT_FOUND.code, status.code)
            assertEquals("Chave não encontrada.", status.description)
        }
    }

    @Test
    fun `Deve dar erro ao inserir codigo do cliente invalido`(){
        val request = RemoverChavePixRequest.newBuilder()
            .setPixId("c56dfef4-7901-44fb-84e2-a2cefb157890")
            .setCodigoInterno("")
            .build()

        val error = assertThrows<StatusRuntimeException> {
            removeChaveClient.removerChave(request)
        }

        with(error){
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("remover.codigoInterno: must not be blank", status.description)
        }
    }

    @Test
    fun `Deve dar erro ao inserir id da chave invalido`(){
        val request = RemoverChavePixRequest.newBuilder()
            .setPixId("")
            .setCodigoInterno("c56dfef4-7901-44fb-84e2-a2cefb157890")
            .build()

        val error = assertThrows<StatusRuntimeException> {
            removeChaveClient.removerChave(request)
        }

        with(error){
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("remover.pixId: must not be blank", status.description)
        }
    }

    @Factory
    class clientGrpc {
        @Singleton
        fun clientPix(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel):
                PixServiceRemoverGrpc.PixServiceRemoverBlockingStub? {
            return PixServiceRemoverGrpc.newBlockingStub(channel)
        }
    }

}