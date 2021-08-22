package br.com.zupacademy.marcosOT6.pix.cadastra

import br.com.zupacademy.marcosOT6.pix.*
import br.com.zupacademy.marcosOT6.pix.TipoDeChave
import br.com.zupacademy.marcosOT6.pix.TipoDeConta
import br.com.zupacademy.marcosOT6.pix.cadastra.requisicao.erp.BuscaInformacoesDaConta
import br.com.zupacademy.marcosOT6.pix.cadastra.requisicao.erp.Conta
import br.com.zupacademy.marcosOT6.pix.cadastra.requisicao.erp.Instituicao
import br.com.zupacademy.marcosOT6.pix.cadastra.requisicao.erp.Titular
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.http.client.exceptions.HttpClientResponseException
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
internal class CadastraNovaChaveEndpointTest(){

    @Inject
    lateinit var repository: ChaveRepository
    @Inject
    lateinit var pixClientGrpc: PixServiceGrpc.PixServiceBlockingStub
    @Inject
    lateinit var buscaInformacoesDaConta: BuscaInformacoesDaConta

    @BeforeEach
    fun setup(){
        repository.deleteAll()
    }

    @Test
    fun `Deve cadastrar uma chave pix aleatoria`(){

        val contaResponse = contaResponse()

        val request = CadastraChavePixRequest
                            .newBuilder()
                            .setCodigoInterno("c56dfef4-7901-44fb-84e2-a2cefb157890")
                            .setTipoDeChave(TipoDeChave.CHAVE_ALEATORIA)
                            .setTipoDeConta(TipoDeConta.CONTA_CORRENTE)
                            .build()

        Mockito
            .`when`(buscaInformacoesDaConta.busca(request.codigoInterno, request.tipoDeConta.name))
            .thenReturn(HttpResponse.ok(contaResponse))

        val response: CadastraChavePixResponse = pixClientGrpc.cadastrarChave(request)
        with(response){
            assertNotNull(pixId)
        }
    }

    @Test
    fun `Deve cadastrar um CPF como chave pix`(){
        val contaResponse = contaResponse()
        val CPF = "05477788823"
        val request = CadastraChavePixRequest
            .newBuilder()
            .setValorDaChave(CPF)
            .setCodigoInterno("c56dfef4-7901-44fb-84e2-a2cefb157890")
            .setTipoDeChave(TipoDeChave.CPF)
            .setTipoDeConta(TipoDeConta.CONTA_CORRENTE)
            .build()

        Mockito
            .`when`(buscaInformacoesDaConta.busca(request.codigoInterno, request.tipoDeConta.name))
            .thenReturn(HttpResponse.ok(contaResponse))

        val response: CadastraChavePixResponse = pixClientGrpc.cadastrarChave(request)
        with(response){
            assertNotNull(pixId)
            assertTrue(repository.existsByValorDaChave(CPF))
        }
    }

    @Test
    fun `Deve cadastrar um Email como chave pix`(){
        val contaResponse = contaResponse()
        val email = "marcos@gmail.com"
        val request = CadastraChavePixRequest
            .newBuilder()
            .setValorDaChave(email)
            .setCodigoInterno("c56dfef4-7901-44fb-84e2-a2cefb157890")
            .setTipoDeChave(TipoDeChave.EMAIL)
            .setTipoDeConta(TipoDeConta.CONTA_CORRENTE)
            .build()

        Mockito
            .`when`(buscaInformacoesDaConta.busca(request.codigoInterno, request.tipoDeConta.name))
            .thenReturn(HttpResponse.ok(contaResponse))

        val response: CadastraChavePixResponse = pixClientGrpc.cadastrarChave(request)
        with(response){
            assertNotNull(pixId)
            assertTrue(repository.existsByValorDaChave(email))
        }
    }

    @Test
    fun `Deve cadastrar um Telefone como chave pix`(){
        val contaResponse = contaResponse()
        val telefone = "+5585988714077"
        val request = CadastraChavePixRequest
            .newBuilder()
            .setValorDaChave(telefone)
            .setCodigoInterno("c56dfef4-7901-44fb-84e2-a2cefb157890")
            .setTipoDeChave(TipoDeChave.TELEFONE)
            .setTipoDeConta(TipoDeConta.CONTA_CORRENTE)
            .build()

        Mockito
            .`when`(buscaInformacoesDaConta.busca(request.codigoInterno, request.tipoDeConta.name))
            .thenReturn(HttpResponse.ok(contaResponse))

        val response: CadastraChavePixResponse = pixClientGrpc.cadastrarChave(request)
        with(response){
            assertNotNull(pixId)
            assertTrue(repository.existsByValorDaChave(telefone))
        }
    }

    /*Cenários de erros*/

    @Test
    fun `Deve dar erro ao cadastrar um chave pix existente`(){

        val email = "marcos@gmail.com"
        //Cenário
        val chaveExistente = ChaveEntidade(
            valorDaChave = email,
            codigoDoCliente = "84e2-a2cefb157890",
            tipoDeChave = br.com.zupacademy.marcosOT6.pix.cadastra.TipoDeChave.EMAIL,
            tipoDeConta = br.com.zupacademy.marcosOT6.pix.cadastra.TipoDeConta.CONTA_CORRENTE,
            conta = ContaAssociada(
                nomeDaInstituicao = "ITAÚ UNIBANCO S.A.",
                ispb = "60701190",
                agencia = "0001",
                numero = "291900",
                nomeDoTitular = "Marcos A Machado",
                cpfDoTitular = "55522233321"
            )
        )
        repository.save(chaveExistente)

        val request = CadastraChavePixRequest
            .newBuilder()
            .setValorDaChave(email) /*Request com mesmo email  cadastrado acima*/
            .setCodigoInterno("c56dfef4-7901-44fb-84e2-a2cefb157890")
            .setTipoDeChave(TipoDeChave.EMAIL)
            .setTipoDeConta(TipoDeConta.CONTA_CORRENTE)
            .build()

        val error = assertThrows<StatusRuntimeException> {
            pixClientGrpc.cadastrarChave(request)
        }

        with(error){
            assertEquals("A chave ${email} já está cadastrada.", status.description)
            assertEquals(Status.ALREADY_EXISTS.code, status.code)
        }

    }

    @Test
    fun `Deve dar erro ao setar um tipo de conta invalido`(){

        val email = "marcos@gmail.com"

        val request = CadastraChavePixRequest
            .newBuilder()
            .setValorDaChave(email) /*Request com mesmo email  cadastrado acima*/
            .setCodigoInterno("c56dfef4-7901-44fb-84e2-a2cefb157890")
            .setTipoDeChave(TipoDeChave.EMAIL)
            .setTipoDeConta(TipoDeConta.UNKNOW_TIPO_CONTA)
            .build()

        val contaResponse = contaResponse()
        Mockito
            .`when`(buscaInformacoesDaConta.busca(request.codigoInterno, request.tipoDeConta.name))
            .thenReturn(HttpResponse.ok(contaResponse))

        val error = assertThrows<StatusRuntimeException> {
            pixClientGrpc.cadastrarChave(request)
        }

        with(error){
            assertEquals("Tipo de Conta desconhecida.", status.description)
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
        }
    }

    @Test
    fun `Deve dar erro ao setar um tipo de chave invalido`(){

        val email = "marcos@gmail.com"

        val request = CadastraChavePixRequest
            .newBuilder()
            .setValorDaChave(email)
            .setCodigoInterno("c56dfef4-7901-44fb-84e2-a2cefb157890")
            .setTipoDeChave(TipoDeChave.UNKNOW_TIPO_CHAVE)
            .setTipoDeConta(TipoDeConta.CONTA_CORRENTE)
            .build()

        val contaResponse = contaResponse()
        Mockito
            .`when`(buscaInformacoesDaConta.busca(request.codigoInterno, request.tipoDeConta.name))
            .thenReturn(HttpResponse.ok(contaResponse))

        val error = assertThrows<StatusRuntimeException> {
            pixClientGrpc.cadastrarChave(request)
        }

        with(error){
            assertEquals("Tipo de Chave desconhecida.", status.description)
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
        }
    }

    @Test
    fun `Deve dar erro ao passar o codigo do cliente em branco`(){

        val email = "marcos@gmail.com"

        val request = CadastraChavePixRequest
            .newBuilder()
            .setValorDaChave(email)
            .setCodigoInterno("")
            .setTipoDeChave(TipoDeChave.EMAIL)
            .setTipoDeConta(TipoDeConta.CONTA_CORRENTE)
            .build()

        val contaResponse = contaResponse()
        Mockito
            .`when`(buscaInformacoesDaConta.busca(request.codigoInterno, request.tipoDeConta.name))
            .thenReturn(HttpResponse.ok(contaResponse))

        val error = assertThrows<StatusRuntimeException> {
            pixClientGrpc.cadastrarChave(request)
        }

        with(error){
            assertEquals("Código do cliente obrigatório.", status.description)
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
        }
    }

    @Test
    fun `Deve dar erro ao passar um CPF mal formatado`(){

        val CPF = "000111222" /*Faltando 2 dígitos*/

        val request = CadastraChavePixRequest
            .newBuilder()
            .setValorDaChave(CPF)
            .setCodigoInterno("c56dfef4-7901-44fb-84e2-a2cefb157890")
            .setTipoDeChave(TipoDeChave.CPF)
            .setTipoDeConta(TipoDeConta.CONTA_CORRENTE)
            .build()

        val contaResponse = contaResponse()
        Mockito
            .`when`(buscaInformacoesDaConta.busca(request.codigoInterno, request.tipoDeConta.name))
            .thenReturn(HttpResponse.ok(contaResponse))

        val error = assertThrows<StatusRuntimeException> {
            pixClientGrpc.cadastrarChave(request)
        }

        with(error){
            assertEquals("save.entity: CPF inválido.", status.description)
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
        }
    }

    @Test
    fun `Deve dar erro ao passar um Email mal formatado`(){

        val email = "marcosgmail.com"

        val request = CadastraChavePixRequest
            .newBuilder()
            .setValorDaChave(email)
            .setCodigoInterno("c56dfef4-7901-44fb-84e2-a2cefb157890")
            .setTipoDeChave(TipoDeChave.EMAIL)
            .setTipoDeConta(TipoDeConta.CONTA_CORRENTE)
            .build()

        val contaResponse = contaResponse()
        Mockito
            .`when`(buscaInformacoesDaConta.busca(request.codigoInterno, request.tipoDeConta.name))
            .thenReturn(HttpResponse.ok(contaResponse))

        val error = assertThrows<StatusRuntimeException> {
            pixClientGrpc.cadastrarChave(request)
        }

        with(error){
            assertEquals("save.entity: Email inválido.", status.description)
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
        }
    }

    @Test
    fun `Deve dar erro ao passar um Telefone mal formatado`(){

        val telefone = "85988714077"

        val request = CadastraChavePixRequest
            .newBuilder()
            .setValorDaChave(telefone)
            .setCodigoInterno("c56dfef4-7901-44fb-84e2-a2cefb157890")
            .setTipoDeChave(TipoDeChave.TELEFONE)
            .setTipoDeConta(TipoDeConta.CONTA_CORRENTE)
            .build()

        val contaResponse = contaResponse()
        Mockito
            .`when`(buscaInformacoesDaConta.busca(request.codigoInterno, request.tipoDeConta.name))
            .thenReturn(HttpResponse.ok(contaResponse))

        val error = assertThrows<StatusRuntimeException> {
            pixClientGrpc.cadastrarChave(request)
        }

        with(error){
            assertEquals("save.entity: Telefone inválido.", status.description)
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
        }
    }

    @Test
    fun `Deve dar erro ao nao encontrar dados da conta no sistema ERP`(){
        val email = "marcos.machado@gmail.com"

        val request = CadastraChavePixRequest
            .newBuilder()
            .setValorDaChave(email)
            .setCodigoInterno("c56dfef4-7901-44fb-84e2-a2cefb157890")
            .setTipoDeChave(TipoDeChave.EMAIL)
            .setTipoDeConta(TipoDeConta.CONTA_CORRENTE)
            .build()

        Mockito
            .`when`(buscaInformacoesDaConta.busca(request.codigoInterno, request.tipoDeConta.name))
            .thenThrow(HttpClientResponseException::class.java)

        val error = assertThrows<StatusRuntimeException> {
            pixClientGrpc.cadastrarChave(request)
        }

        with(error){
            assertEquals("Conta não encontrada no sistema Itaú.", status.description)
            assertEquals(Status.NOT_FOUND.code, status.code)
        }
    }



    private fun contaResponse(): Conta {
         return Conta(
            agencia = "0001",
            numero = "291900",
            titular = Titular(nome = "Rafael M C Ponte", cpf = "02467781054"),
            instituicao = Instituicao(nome = "ITAÚ UNIBANCO S.A.", ispb = "60701190")
        )
    }

    /*Mockando BuscaInformacoesDaConta para requisições ao ERP*/
    @MockBean(BuscaInformacoesDaConta::class) /*Informa a classe para substituir pelo o Mock*/
    fun mockBuscaInformacoesDaConta(): BuscaInformacoesDaConta? {
        return Mockito.mock(BuscaInformacoesDaConta::class.java)
    }

    /*Criando o client Grpc*/
    @Factory
    class clientGrpc {
        @Singleton
        fun clientPix(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): PixServiceGrpc.PixServiceBlockingStub? {
            return PixServiceGrpc.newBlockingStub(channel)
        }
    }

}