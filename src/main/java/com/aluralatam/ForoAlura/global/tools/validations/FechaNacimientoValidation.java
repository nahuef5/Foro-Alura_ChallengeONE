package com.aluralatam.ForoAlura.global.tools.validations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.Period;

public class FechaNacimientoValidation implements ConstraintValidator<FechaNacimientoValid, LocalDate> {
    private int minNacimiento;
    private int maxNacimiento;
    /**
     * @param fechaNacimientoValid
     */
    @Override
    public void initialize(FechaNacimientoValid fechaNacimientoValid) {
        this.minNacimiento=fechaNacimientoValid.minAge();
        this.maxNacimiento=fechaNacimientoValid.maxAge();
    }
    /**
     * @param nacimiento
     * @param constraintValidatorContext
     * @return
     */
    @Override
    public boolean isValid(LocalDate nacimiento, ConstraintValidatorContext constraintValidatorContext) {
        if(nacimiento==null)
            return false;
        LocalDate hoy=LocalDate.now();
        Period period= Period.between(nacimiento, hoy);
        return period.getYears()>=minNacimiento && period.getYears()<=maxNacimiento;
    }
}
