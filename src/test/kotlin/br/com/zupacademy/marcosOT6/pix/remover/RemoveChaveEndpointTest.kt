package br.com.zupacademy.marcosOT6.pix.remover

import br.com.zupacademy.marcosOT6.pix.PixServiceRemoverGrpc
import br.com.zupacademy.marcosOT6.pix.RemoverChavePixRequest
import br.com.zupacademy.marcosOT6.pix.cadastra.*
import br.com.zupacademy.marcosOT6.pix.remover.requisicao.bcb.RemoveChavePixNoBCB
import br.com.zupacademy.marcosOT6.pix.remover.requisicao.bcb.dto.RemoveChaveRequest
import br.com.zupacademy.marcosOT6.pix.remover.requisicao.bcb.dto.RemoveChaveResponse
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest(transactional = false)
internal class RemoveChaveEndpointTest() {

    @Inject
    lateinit var removeChaveClient: PixServiceRemoverGrpc.PixServiceRemoverBlockingStub

    @Inject
    lateinit var repository: ChaveRepository

    @Inject
    lateinit var removeChavePixNoBCB: RemoveChavePixNoBCB

    @BeforeEach
    fun setup(){
        repository.deleteAll()
    }

    @Test
    fun `Deve remover uma chave pix existente`() {
        //Cenário
        val email = "marcos@gmail.com"
        val removeRequest = RemoveChaveRequest(email, "60701190")
        val removeResponse = RemoveChaveResponse(email, "60701190", LocalDateTime.now().toString())
        Mockito.`when`(removeChavePixNoBCB.remove(email, removeRequest)).thenReturn(HttpResponse.ok(removeResponse))

        val chave = insereChave()

        val response = removeChaveClient.removerChave(
            RemoverChavePixRequest
                .newBuilder()
                .setCodigoInterno(chave.codigoDoCliente)
                .setPixId(chave.chaveId.toString())
                .build()
        )

        with(response){
            assertEquals(chave.chaveId, UUID.fromString(pixId))
            assertEquals(chave.codigoDoCliente, codigoInterno)
            assertTrue(!repository.existsByValorDaChave(chave.valorDaChave))
        }
    }

    @Test
    fun `Deve dar erro ao remover uma chave que nao exista`() {

        val request = RemoverChavePixRequest.newBuilder()
            .setPixId("c56dfef4-7901-44fb-84e2-a2cefb157890")
            .setCodigoInterno("44fb-84e2-a2cefb157890")
            .build()

        val error = assertThrows<StatusRuntimeException> {
            removeChaveClient.removerChave(request)
        }

        with(error) {
            assertEquals(Status.NOT_FOUND.code, status.code)
            assertEquals("Chave não encontrada.", status.description)
        }
    }

    @Test
    fun `Deve dar erro ao inserir codigo do cliente invalido`() {
        val request = RemoverChavePixRequest.newBuilder()
            .setPixId("c56dfef4-7901-44fb-84e2-a2cefb157890")
            .setCodigoInterno("")
            .build()

        val error = assertThrows<StatusRuntimeException> {
            removeChaveClient.removerChave(request)
        }

        with(error) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("remover.codigoInterno: must not be blank", status.description)
        }
    }

    @Test
    fun `Deve dar erro ao inserir id da chave invalido`() {
        val request = RemoverChavePixRequest.newBuilder()
            .setPixId("")
            .setCodigoInterno("c56dfef4-7901-44fb-84e2-a2cefb157890")
            .build()

        val error = assertThrows<StatusRuntimeException> {
            removeChaveClient.removerChave(request)
        }

        with(error) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("remover.pixId: must not be blank", status.description)
        }
    }

    fun insereChave(): ChaveEntidade {
        val codigoDoCliente = "c56dfef4-7901-44fb-84e2-a2cefb157890"
        val chave = ChaveEntidade(
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
        return repository.save(chave)
    }

    @MockBean(RemoveChavePixNoBCB::class)
    fun mockRemoveChavePixNoBCB(): RemoveChavePixNoBCB {
        return Mockito.mock(RemoveChavePixNoBCB::class.java)
    }

    @Factory
    class ClientGrpc {
        @Singleton
        fun clientPix(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel):
                PixServiceRemoverGrpc.PixServiceRemoverBlockingStub? {
            return PixServiceRemoverGrpc.newBlockingStub(channel)
        }
    }

}