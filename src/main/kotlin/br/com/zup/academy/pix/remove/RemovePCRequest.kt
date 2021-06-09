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
    @field:NotNull
    @ValidUUID
    val clienteId: String,
    @field:NotNull
    @ValidUUID
    val pixId: String
)
{


}