package br.com.zupacademy.marcosOT6.pix.cadastra

import br.com.zupacademy.marcosOT6.pix.validacao.ChavePix
import java.util.*
import javax.persistence.*

@Entity
@ChavePix
@Table(name = "chave")
data class ChaveEntidade(
    val valorDaChave: String,
    val codigoDoCliente: String,
    @field:Enumerated(EnumType.STRING) val tipoDeChave: TipoDeChave,
    @field:Enumerated(EnumType.STRING) val tipoDeConta: TipoDeConta,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var chaveId: UUID? = null
}
