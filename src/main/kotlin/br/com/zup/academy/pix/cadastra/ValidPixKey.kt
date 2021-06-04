package br.com.zup.academy

import br.com.zup.academy.pix.ChavePix
import javax.inject.Singleton
import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import javax.validation.Payload
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.*
import kotlin.reflect.KClass

@MustBeDocumented
@Target(CLASS, TYPE)
@Retention(RUNTIME)
@Constraint(validatedBy = [ValidPixKeyValidador::class])
annotation class ValidPixKey(
    val message: String = "chave pix inválida",
    val groups: Array<KClass<Any>> = [],
    val payload:  Array<KClass<Payload>> = []
)

@Singleton
class ValidPixKeyValidador: ConstraintValidator<ValidPixKey, ChavePix>{

    override fun isValid(value: ChavePix?, context: ConstraintValidatorContext?): Boolean {
        if(value?.tipoChave== null)
            return false

        return value.tipoChave.valida(value.valorChave)
    }

}