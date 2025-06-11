package airportsystem.mscliente.validator;

import airportsystem.mscliente.model.CPF;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.InputMismatchException;

public class CPFValidator implements ConstraintValidator<CPF, String> {

    @Override
    public void initialize(CPF constraintAnnotation) {
        // Initialization if needed
    }

    @Override
    public boolean isValid(String cpf, ConstraintValidatorContext context) {
        if (cpf == null || cpf.isEmpty()) {
            return false;
        }

        // Remove special characters
        cpf = cpf.replaceAll("[^0-9]", "");

        // Must be 11 digits
        if (cpf.length() != 11) {
            return false;
        }

        // Check for repeated digits (all the same digit is invalid)
        boolean allDigitsEqual = true;
        for (int i = 1; i < cpf.length(); i++) {
            if (cpf.charAt(i) != cpf.charAt(0)) {
                allDigitsEqual = false;
                break;
            }
        }
        if (allDigitsEqual) {
            return false;
        }

        // First verification digit calculation
        int sum = 0;
        for (int i = 0; i < 9; i++) {
            sum += (cpf.charAt(i) - '0') * (10 - i);
        }
        int remainder = sum % 11;
        int firstVerificationDigit = (remainder < 2) ? 0 : 11 - remainder;

        // Check first verification digit
        if ((cpf.charAt(9) - '0') != firstVerificationDigit) {
            return false;
        }

        // Second verification digit calculation
        sum = 0;
        for (int i = 0; i < 10; i++) {
            sum += (cpf.charAt(i) - '0') * (11 - i);
        }
        remainder = sum % 11;
        int secondVerificationDigit = (remainder < 2) ? 0 : 11 - remainder;

        // Check second verification digit
        return (cpf.charAt(10) - '0') == secondVerificationDigit;
    }
}