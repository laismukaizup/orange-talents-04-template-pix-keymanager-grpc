package br.com.zup.academy.pix.remove

import br.com.zup.academy.ValidPixKey
import br.com.zup.academy.pix.ValidUUID
import io.micronaut.core.annotation.Introspected
import org.hibernate.annotations.Type
import org.hibernate.type.UUIDCharType
import java.util.*
import javax.validation.constraints.NotNull

@Introspected
class RemoveCPRequest(
    @ValidUUID
    @field:NotNull
    val clienteId: String,
    @ValidUUID
    @field:NotNull
    val pixId: String
)
{


}