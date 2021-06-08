package br.com.zup.academy.pix

import br.com.zup.academy.pix.modelo.ChavePix
import io.micronaut.context.annotation.Executable
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import java.util.*

@Repository
interface ChavePixRepository : JpaRepository<ChavePix, UUID> {
    fun existsByValorChave(valorChave: String): Boolean
    @Executable
    fun find(idCliente: UUID, id: UUID): Optional<ChavePix>
    fun findByIdCliente(idCliente: UUID) : Optional<ChavePix>
    //fun findById(id: UUID) : Optional<ChavePix>

}
