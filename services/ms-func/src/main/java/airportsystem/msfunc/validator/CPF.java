package airportsystem.msfunc.validator;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = CPF.CPFValidatorImpl.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CPF {
    String message() default "CPF inválido";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    class CPFValidatorImpl implements ConstraintValidator<CPF, String> {
        @Override
        public void initialize(CPF constraintAnnotation) {
        }

        @Override
        public boolean isValid(String cpf, ConstraintValidatorContext context) {
            if (cpf == null || cpf.trim().isEmpty()) {
                return true; // Let @NotBlank handle null/empty validation
            }

            // Remove non-digits
            cpf = cpf.replaceAll("\\D", "");

            // Check if it has 11 digits
            if (cpf.length() != 11) {
                return false;
            }

            // Check if all digits are the same
            if (cpf.matches("(\\d)\\1{10}")) {
                return false;
            }

            // Validate CPF algorithm
            try {
                int[] digits = new int[11];
                for (int i = 0; i < 11; i++) {
                    digits[i] = Integer.parseInt(cpf.substring(i, i + 1));
                }

                // Calculate first check digit
                int sum = 0;
                for (int i = 0; i < 9; i++) {
                    sum += digits[i] * (10 - i);
                }
                int remainder = sum % 11;
                int checkDigit1 = remainder < 2 ? 0 : 11 - remainder;

                if (digits[9] != checkDigit1) {
                    return false;
                }

                // Calculate second check digit
                sum = 0;
                for (int i = 0; i < 10; i++) {
                    sum += digits[i] * (11 - i);
                }
                remainder = sum % 11;
                int checkDigit2 = remainder < 2 ? 0 : 11 - remainder;

                return digits[10] == checkDigit2;
            } catch (NumberFormatException e) {
                return false;
            }
        }
    }
} 