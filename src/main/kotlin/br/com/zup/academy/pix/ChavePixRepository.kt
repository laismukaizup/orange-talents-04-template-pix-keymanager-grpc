package br.com.zup.academy.pix

import br.com.zup.academy.pix.modelo.ChavePix
import io.micronaut.context.annotation.Executable
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import java.util.*

@Repository
interface ChavePixRepository : JpaRepository<ChavePix, String> {
    fun existsByValorChave(valorChave: String): Boolean
    @Executable
    fun find(idCliente: String, id: String): Optional<ChavePix>
    fun findByIdCliente(idCliente: String) : Optional<ChavePix>
}
