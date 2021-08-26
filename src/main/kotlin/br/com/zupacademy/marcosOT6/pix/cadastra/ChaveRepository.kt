package br.com.zupacademy.marcosOT6.pix.cadastra

import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import java.util.*

@Repository
interface ChaveRepository : JpaRepository<ChaveEntidade, UUID> {
    fun findByChaveIdAndCodigoDoCliente(idChave: UUID, idCliente: String): Optional<ChaveEntidade>
    fun existsByValorDaChave(valorDaChave: String): Boolean
    fun findByValorDaChave(valorDaChave: String): Optional<ChaveEntidade>
    fun findByCodigoDoCliente(idCliente: String): List<ChaveEntidade>
}