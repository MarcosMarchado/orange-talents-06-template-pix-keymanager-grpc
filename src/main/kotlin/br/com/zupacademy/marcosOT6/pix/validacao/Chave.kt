package br.com.zupacademy.marcosOT6.pix.validacao

import br.com.zupacademy.marcosOT6.pix.cadastra.ChaveEntidade
import br.com.zupacademy.marcosOT6.pix.cadastra.TipoDeChave
import io.micronaut.core.annotation.AnnotationValue
import io.micronaut.validation.validator.constraints.ConstraintValidator
import io.micronaut.validation.validator.constraints.ConstraintValidatorContext
import javax.inject.Singleton
import javax.validation.Constraint
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.CLASS

@MustBeDocumented
@Target(CLASS)
@Retention(RUNTIME)
@Constraint(validatedBy = [ChaveValidator::class])
annotation class ChavePix(val message: String = "Valor não atende ao formato selecionado.")

@Singleton
class ChaveValidator : ConstraintValidator<ChavePix, ChaveEntidade> {
    override fun isValid(
        value: ChaveEntidade?,
        annotationMetadata: AnnotationValue<ChavePix>,
        context: ConstraintValidatorContext
    ): Boolean {

        when(value?.tipoDeChave){
            TipoDeChave.TELEFONE -> return value.valorDaChave.matches("^\\+[1-9][0-9]\\d{1,14}$".toRegex())

            TipoDeChave.CPF -> return value.valorDaChave.matches("^[0-9]{11}\$".toRegex())

            TipoDeChave.EMAIL -> return value.valorDaChave.contains("@")

            else -> return true
        }

    }


}
