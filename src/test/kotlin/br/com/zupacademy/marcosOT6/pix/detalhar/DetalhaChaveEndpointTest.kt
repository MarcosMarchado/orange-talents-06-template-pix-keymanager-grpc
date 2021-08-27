package br.com.zupacademy.marcosOT6.pix.detalhar

import br.com.zupacademy.marcosOT6.pix.DetalhesChavePixRequest
import br.com.zupacademy.marcosOT6.pix.DetalhesChavePixResponse
import br.com.zupacademy.marcosOT6.pix.PixServiceDetalhesGrpc
import br.com.zupacademy.marcosOT6.pix.cadastra.*
import br.com.zupacademy.marcosOT6.pix.detalhar.requisicao.bcb.BankAccount
import br.com.zupacademy.marcosOT6.pix.detalhar.requisicao.bcb.BuscaDetalhesChavePix
import br.com.zupacademy.marcosOT6.pix.detalhar.requisicao.bcb.DetalhesDaChavePixResponseBCB
import br.com.zupacademy.marcosOT6.pix.detalhar.requisicao.bcb.Owner
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.Assert.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest(transactional = false)
internal class DetalhaChaveEndpointTest {

    @Inject
    lateinit var repository: ChaveRepository

    @Inject
    lateinit var buscaDetalhesChavePix: BuscaDetalhesChavePix

    @Inject
    lateinit var client: PixServiceDetalhesGrpc.PixServiceDetalhesBlockingStub

    @BeforeEach
    fun setup() {
        repository.deleteAll()
    }

    /*Cenários de sucesso*/
    @Test
    fun `Deve buscar detalhes de uma chave atraves do idCliente e idChave`() {
        /*Cenário*/
        val chave = registraChaveNoBancoDeDados()

        /*Ação*/

        val filtroPorPixIdRequest = DetalhesChavePixRequest
            .FiltroPorPixId
            .newBuilder()
            .setCodigoInterno(chave.codigoDoCliente)
            .setPixId(chave.chaveId.toString())
            .build()

        val request = DetalhesChavePixRequest
            .newBuilder()
            .setKeyManager(filtroPorPixIdRequest)
            .build()

        val response = client.detalharChave(request)

        /*Asserts*/
        with(response) {
            assertEquals(chave.chaveId.toString(), pixId)
            assertEquals(chave.codigoDoCliente, codigoInterno)
            assertEquals(chave.tipoDeChave.name, tipoDeChave.name)
            assertEquals(chave.valorDaChave, valorDaChave)
            assertEquals(chave.conta.nomeDoTitular, conta.titular)
            assertEquals(chave.conta.cpfDoTitular, conta.cpf)
            assertEquals(chave.conta.nomeDaInstituicao, conta.nomeDaInstituicao)
            assertEquals(chave.conta.agencia, conta.agencia)
            assertEquals(chave.conta.numero, conta.numero)
            assertEquals(chave.tipoDeConta.name, conta.tipoDeConta.name)
        }
    }

    @Test
    fun `Deve buscar detalhes de uma chave localmente atraves do valor da chave`() {
        /*Cenário*/
        val chave = registraChaveNoBancoDeDados()

        /*Ação*/
        val request = DetalhesChavePixRequest.newBuilder().setValorDaChave(chave.valorDaChave).build()
        val response = client.detalharChave(request)
        /*Asserts*/
        with(response) {
            assertEquals(chave.chaveId.toString(), pixId)
            assertEquals(chave.codigoDoCliente, codigoInterno)
            assertEquals(chave.tipoDeChave.name, tipoDeChave.name)
            assertEquals(chave.valorDaChave, valorDaChave)
            assertEquals(chave.conta.nomeDoTitular, conta.titular)
            assertEquals(chave.conta.cpfDoTitular, conta.cpf)
            assertEquals(chave.conta.nomeDaInstituicao, conta.nomeDaInstituicao)
            assertEquals(chave.conta.agencia, conta.agencia)
            assertEquals(chave.conta.numero, conta.numero)
            assertEquals(chave.tipoDeConta.name, conta.tipoDeConta.name)
        }
    }

    @Test
    fun `Deve buscar detalhes de uma chave no BCB atraves do valor da chave quando nao for encontrado localmente`() {
        /*Cenário*/
        val responseBCB: DetalhesDaChavePixResponseBCB = responseBCB()
        val chaveDoTipoEmail = "marcos@gmail.com"
        Mockito.`when`(buscaDetalhesChavePix.busca(chaveDoTipoEmail)).thenReturn(HttpResponse.ok(responseBCB))

        /*Ação*/
        val request = DetalhesChavePixRequest.newBuilder().setValorDaChave(chaveDoTipoEmail).build()
        val chave = responseBCB.toModel()
        val response: DetalhesChavePixResponse = client.detalharChave(request)

        with(response) {
            assertEquals("", pixId)
            assertEquals("", codigoInterno)
            assertEquals(chave.tipoDeChave.name, tipoDeChave.name)
            assertEquals(chave.valorDaChave, valorDaChave)
            assertEquals(chave.conta.nomeDoTitular, conta.titular)
            assertEquals(chave.conta.cpfDoTitular, conta.cpf)
            assertEquals(chave.conta.nomeDaInstituicao, conta.nomeDaInstituicao)
            assertEquals(chave.conta.agencia, conta.agencia)
            assertEquals(chave.conta.numero, conta.numero)
            assertEquals(chave.tipoDeConta.name, conta.tipoDeConta.name)
        }

    }

    /*Cenários de erros*/
    @Test
    fun `Deve dar erro ao passar as formas de busca como nulas`() {

        val request = DetalhesChavePixRequest
            .newBuilder()
            .build()

        val error = assertThrows<StatusRuntimeException> {
            client.detalharChave(request)
        }

        with(error) {
            assertEquals("Pelo menos uma das formas de busca é obrigatória.", status.description)
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
        }

    }

    @Test /*Para buscas no Keymanager*/
    fun `Deve dar erro ao passar o codigoInterno de busca em branco ou nulo`() {

        val filtroPorPixId = DetalhesChavePixRequest
            .FiltroPorPixId
            .newBuilder()
            .setCodigoInterno("")
            .setPixId("0a6b10ea-8af8-46cf-9da8-4727ab08ae16")
            .build()

        val request = DetalhesChavePixRequest
            .newBuilder()
            .setKeyManager(filtroPorPixId)
            .build()

        val error = assertThrows<StatusRuntimeException> {
            client.detalharChave(request)
        }

        with(error) {
            assertEquals("Código do cliente obrigatório.", status.description)
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
        }
    }

    @Test /*Para buscas no Keymanager*/
    fun `Deve dar erro ao passar o pixId de busca em branco ou nulo`() {

        val filtroPorPixId = DetalhesChavePixRequest
            .FiltroPorPixId
            .newBuilder()
            .setCodigoInterno("0a6b10ea-58b2-46cf-9da8-4727ab08ae16")
            .setPixId("")
            .build()

        val request = DetalhesChavePixRequest
            .newBuilder()
            .setKeyManager(filtroPorPixId)
            .build()

        val error = assertThrows<StatusRuntimeException> {
            client.detalharChave(request)
        }

        with(error) {
            assertEquals("Identificador da chave obrigatório.", status.description)
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
        }
    }

    @Test /*Para buscas no Keymanager*/
    fun `Deve dar erro ao passar um pixId invalido`() {
        val filtroPorPixId = DetalhesChavePixRequest
            .FiltroPorPixId
            .newBuilder()
            .setCodigoInterno("0a6b10ea-58b2-46cf-9da8-4727ab08ae16")
            .setPixId("0-00005as12-asas") /*Inválido*/
            .build()

        val request = DetalhesChavePixRequest
            .newBuilder()
            .setKeyManager(filtroPorPixId)
            .build()

        val error = assertThrows<StatusRuntimeException> {
            client.detalharChave(request)
        }

        with(error) {
            assertEquals("O valor PixId não atende a um formato válido.", status.description)
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
        }
    }

    @Test /*Para buscas no Keymanager*/
    fun `Deve dar erro ao nao encontrar a chave pix no sistema interno`() {

        val filtroPorPixId = DetalhesChavePixRequest
            .FiltroPorPixId
            .newBuilder()
            .setCodigoInterno("0a6b10ea-58b2-46cf-9da8-4727ab08ae16")
            .setPixId("c56dfef4-7901-44fb-84e2-a2cefb157890")
            .build()

        val request = DetalhesChavePixRequest
            .newBuilder()
            .setKeyManager(filtroPorPixId)
            .build()

        val error = assertThrows<StatusRuntimeException> {
            client.detalharChave(request)
        }

        with(error){
            assertEquals("Chave não encontrada.", status.description)
            assertEquals(Status.NOT_FOUND.code, status.code)
        }


    }

    @Test
    fun `Deve dar erro ao nao encontrar a chave no sistema BCB`(){

        val request = DetalhesChavePixRequest
            .newBuilder()
            .setValorDaChave("marcos@gmail.com")
            .build()

        //Mockar o client BCB
        Mockito.`when`(buscaDetalhesChavePix.busca(request.valorDaChave)).thenReturn(HttpResponse.notFound())

        val error = assertThrows<StatusRuntimeException> {
            client.detalharChave(request)
        }

        with(error){
            assertEquals("Chave não encontrada no BCB.", status.description)
            assertEquals(Status.NOT_FOUND.code, status.code)
        }
    }

    fun registraChaveNoBancoDeDados(): ChaveEntidade {
        val chave = ChaveEntidade(
            valorDaChave = "marcos@gmail.com",
            codigoDoCliente = "c56dfef4-7901-44fb-84e2-a2cefb157890",
            tipoDeChave = TipoDeChave.EMAIL,
            tipoDeConta = TipoDeConta.CONTA_CORRENTE,
            conta = ContaAssociada(
                nomeDaInstituicao = "ITAÚ UNIBANCO S.A.",
                ispb = "60701190",
                agencia = "0001",
                numero = "291900",
                nomeDoTitular = "Marcos Machado",
                cpfDoTitular = "00055522278"
            )
        )
        return repository.save(chave)
    }

    fun responseBCB(): DetalhesDaChavePixResponseBCB {
        return DetalhesDaChavePixResponseBCB(
            keyType = "EMAIL",
            key = "marcos@gmail.com",
            bankAccount = BankAccount(
                participant = "60701190",
                branch = "0001",
                accountNumber = "123456",
                accountType = "CACC"
            ),
            owner = Owner(
                type = "NATURAL_PERSON",
                name = "Marcos Machado",
                taxIdNumber = "00055522278"
            ),
            createdAt = "2021-08-25T12:53:16.935072"
        )
    }

    @MockBean(BuscaDetalhesChavePix::class)
    fun buscaDetalhesChavePixBCB(): BuscaDetalhesChavePix {
        return Mockito.mock(BuscaDetalhesChavePix::class.java)
    }

    @Factory
    class ClientGrpc {
        @Singleton
        fun blockingStubConfig(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel):
                PixServiceDetalhesGrpc.PixServiceDetalhesBlockingStub {
            return PixServiceDetalhesGrpc.newBlockingStub(channel)
        }
    }


}