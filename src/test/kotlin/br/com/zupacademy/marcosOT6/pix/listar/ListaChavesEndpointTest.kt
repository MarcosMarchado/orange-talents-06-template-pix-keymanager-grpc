package br.com.zupacademy.marcosOT6.pix.listar

import br.com.zupacademy.marcosOT6.pix.ListarChavesPixRequest
import br.com.zupacademy.marcosOT6.pix.ListarChavesPixResponse
import br.com.zupacademy.marcosOT6.pix.PixServiceListagemGrpc
import br.com.zupacademy.marcosOT6.pix.cadastra.*
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest(transactional = false)
internal class ListaChavesEndpointTest {

    @Inject
    lateinit var repository: ChaveRepository

    @Inject
    lateinit var client: PixServiceListagemGrpc.PixServiceListagemBlockingStub

    @BeforeEach
    fun setup() {
        repository.deleteAll()
    }

    @Test
    fun `Deve listar as chaves pix de um cliente`() {
        val chaves = registraChaveNoBancoDeDados()
        val codigoDoCliente = "c56dfef4-7901-44fb-84e2-a2cefb157890"

        val request = ListarChavesPixRequest
            .newBuilder()
            .setCodigoInterno(codigoDoCliente)
            .build()

        val response: ListarChavesPixResponse = client.listarChaves(request)

        with(response) {
            assertEquals(chaves.size, chavesCount)
            assertEquals(chaves[0].valorDaChave, getChaves(0).valorDaChave)
            assertEquals(chaves[1].valorDaChave, getChaves(1).valorDaChave)
            assertEquals(chaves[2].valorDaChave, getChaves(2).valorDaChave)
        }
    }

    @Test
    fun `Deve trazer uma lista vazia quando nao houver chaves cadastras`() {

        val codigoDoCliente = "c56dfef4-7901-44fb-84e2-a2cefb157890"

        val request = ListarChavesPixRequest
            .newBuilder()
            .setCodigoInterno(codigoDoCliente)
            .build()

        val response: ListarChavesPixResponse = client.listarChaves(request)

        assertTrue(response.chavesCount == 0)
    }

    @Test
    fun `Deve dar erro ao passar um valor invalido`() {

        val codigoDoCliente = ""

        val request = ListarChavesPixRequest
            .newBuilder()
            .setCodigoInterno(codigoDoCliente)
            .build()

        val error = assertThrows<StatusRuntimeException> {
            client.listarChaves(request)
        }

        with(error){
            assertEquals("Código do cliente obrigatório.", status.description)
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
        }
    }



    fun registraChaveNoBancoDeDados(): List<ChaveEntidade> {
        val contaAssociada = ContaAssociada(
            nomeDaInstituicao = "ITAÚ UNIBANCO S.A.",
            ispb = "60701190",
            agencia = "0001",
            numero = "291900",
            nomeDoTitular = "Marcos Machado",
            cpfDoTitular = "00055522278"
        )

        val chaves: List<ChaveEntidade> = listOf(
            ChaveEntidade(
                valorDaChave = "05488800021",
                codigoDoCliente = "c56dfef4-7901-44fb-84e2-a2cefb157890",
                tipoDeChave = TipoDeChave.CPF,
                tipoDeConta = TipoDeConta.CONTA_CORRENTE,
                conta = contaAssociada
            ),
            ChaveEntidade(
                valorDaChave = "marcos@gmail.com",
                codigoDoCliente = "c56dfef4-7901-44fb-84e2-a2cefb157890",
                tipoDeChave = TipoDeChave.EMAIL,
                tipoDeConta = TipoDeConta.CONTA_CORRENTE,
                conta = contaAssociada
            ),
            ChaveEntidade(
                valorDaChave = "+559898785410",
                codigoDoCliente = "c56dfef4-7901-44fb-84e2-a2cefb157890",
                tipoDeChave = TipoDeChave.TELEFONE,
                tipoDeConta = TipoDeConta.CONTA_CORRENTE,
                conta = contaAssociada
            )
        )
        return repository.saveAll(chaves)
    }

    @Factory
    class ClientGrpc {
        @Singleton
        fun blockingStubConfig(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel):
                PixServiceListagemGrpc.PixServiceListagemBlockingStub {
            return PixServiceListagemGrpc.newBlockingStub(channel)
        }
    }
}