package br.com.zupacademy.marcosOT6.pix.cadastra

import br.com.zupacademy.marcosOT6.pix.validacao.ChavePix
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Entity
@ChavePix
@Table(name = "chave")
data class ChaveEntidade(
    @field:Size(max = 77) val valorDaChave: String,
    @field:NotBlank val codigoDoCliente: String,
    @field:Enumerated(EnumType.STRING) @NotBlank val tipoDeChave: TipoDeChave,
    @field:Enumerated(EnumType.STRING) @NotBlank val tipoDeConta: TipoDeConta,
    @field:Embedded val conta: ContaAssociada
) {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var chaveId: UUID? = null
}
