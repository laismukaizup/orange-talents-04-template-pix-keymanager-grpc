package br.com.zup.academy

import br.com.zup.academy.pix.cadastra.ChavePixRequest
import javax.inject.Singleton
import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import javax.validation.Payload
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.CLASS
import kotlin.annotation.AnnotationTarget.TYPE
import kotlin.reflect.KClass

@MustBeDocumented
@Target(CLASS, TYPE)
@Retention(RUNTIME)
@Constraint(validatedBy = [ValidPixKeyValidador::class])
annotation class ValidPixKey(
    val message: String = "chave pix inválida. (\${validatedValue.tipoChave})",
    val groups: Array<KClass<Any>> = [],
    val payload:  Array<KClass<Payload>> = []
)

@Singleton
class ValidPixKeyValidador: ConstraintValidator<ValidPixKey, ChavePixRequest>{

    override fun isValid(value: ChavePixRequest?, context: ConstraintValidatorContext?): Boolean {
        if(value?.tipoChave== null)
            return false
        return value.tipoChave.valida(value.valorChave)
    }

}