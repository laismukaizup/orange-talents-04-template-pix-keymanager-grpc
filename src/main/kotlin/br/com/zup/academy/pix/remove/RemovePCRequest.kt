package br.com.zup.academy.pix.remove

import org.hibernate.annotations.Type
import org.hibernate.type.UUIDCharType
import java.util.*
import javax.validation.constraints.NotNull

class RemoveCPRequest(
    @field:NotNull
    val clienteId: UUID,
    @field:NotNull
    val pixId: UUID
)
{


}