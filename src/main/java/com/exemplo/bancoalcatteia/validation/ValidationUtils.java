package com.exemplo.bancoalcatteia.validation;

import com.exemplo.bancoalcatteia.exception.BusinessException;

import java.time.LocalDate;
import java.util.Collection;

/**
 * Utilitários para validações comuns em toda a aplicação
 */
public class ValidationUtils {

    /**
     * Valida se um valor não é nulo
     */
    public static void validateNotNull(Object value, String fieldName) {
        if (value == null) {
            throw new BusinessException(fieldName + " é obrigatório");
        }
    }

    /**
     * Valida se uma string não é nula ou vazia
     */
    public static void validateNotEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new BusinessException(fieldName + " é obrigatório");
        }
    }

    /**
     * Valida se uma string não excede o tamanho máximo
     */
    public static void validateMaxLength(String value, int maxLength, String fieldName) {
        if (value != null && value.length() > maxLength) {
            throw new BusinessException(fieldName + " não pode exceder " + maxLength + " caracteres");
        }
    }

    /**
     * Valida se uma string tem tamanho mínimo
     */
    public static void validateMinLength(String value, int minLength, String fieldName) {
        if (value != null && value.length() < minLength) {
            throw new BusinessException(fieldName + " deve ter pelo menos " + minLength + " caracteres");
        }
    }

    /**
     * Valida se um ID é positivo
     */
    public static void validatePositiveId(Integer id, String fieldName) {
        if (id != null && id <= 0) {
            throw new BusinessException(fieldName + " deve ser um número positivo");
        }
    }

    /**
     * Valida se uma data não é no passado
     */
    public static void validateNotPastDate(LocalDate date, String fieldName) {
        if (date != null && date.isBefore(LocalDate.now())) {
            throw new BusinessException(fieldName + " não pode ser uma data no passado");
        }
    }

    /**
     * Valida se uma data não é no futuro
     */
    public static void validateNotFutureDate(LocalDate date, String fieldName) {
        if (date != null && date.isAfter(LocalDate.now())) {
            throw new BusinessException(fieldName + " não pode ser uma data no futuro");
        }
    }

    /**
     * Valida se um valor está dentro de uma lista de valores válidos
     */
    public static void validateInList(String value, Collection<String> validValues, String fieldName) {
        if (value != null && !validValues.contains(value.toUpperCase())) {
            throw new BusinessException(fieldName + " inválido. Valores permitidos: " + 
                String.join(", ", validValues));
        }
    }

    /**
     * Valida se um número é positivo
     */
    public static void validatePositiveNumber(Number value, String fieldName) {
        if (value != null && value.doubleValue() <= 0) {
            throw new BusinessException(fieldName + " deve ser um número positivo");
        }
    }

    /**
     * Valida se um número não é negativo
     */
    public static void validateNonNegativeNumber(Number value, String fieldName) {
        if (value != null && value.doubleValue() < 0) {
            throw new BusinessException(fieldName + " não pode ser negativo");
        }
    }

    /**
     * Valida formato de email simples
     */
    public static void validateEmail(String email, String fieldName) {
        if (email != null && !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new BusinessException(fieldName + " deve ter um formato válido");
        }
    }

    /**
     * Valida se uma coleção não está vazia
     */
    public static void validateNotEmpty(Collection<?> collection, String fieldName) {
        if (collection == null || collection.isEmpty()) {
            throw new BusinessException(fieldName + " não pode estar vazio");
        }
    }

    /**
     * Valida se um valor está dentro de um range
     */
    public static void validateRange(Number value, Number min, Number max, String fieldName) {
        if (value != null) {
            double val = value.doubleValue();
            double minVal = min != null ? min.doubleValue() : Double.MIN_VALUE;
            double maxVal = max != null ? max.doubleValue() : Double.MAX_VALUE;
            
            if (val < minVal || val > maxVal) {
                throw new BusinessException(fieldName + " deve estar entre " + minVal + " e " + maxVal);
            }
        }
    }

    /**
     * Valida se uma data está dentro de um período
     */
    public static void validateDateRange(LocalDate date, LocalDate startDate, LocalDate endDate, String fieldName) {
        if (date != null) {
            if (startDate != null && date.isBefore(startDate)) {
                throw new BusinessException(fieldName + " não pode ser anterior a " + startDate);
            }
            if (endDate != null && date.isAfter(endDate)) {
                throw new BusinessException(fieldName + " não pode ser posterior a " + endDate);
            }
        }
    }

    /**
     * Valida se uma string contém apenas caracteres alfanuméricos
     */
    public static void validateAlphanumeric(String value, String fieldName) {
        if (value != null && !value.matches("^[a-zA-Z0-9]*$")) {
            throw new BusinessException(fieldName + " deve conter apenas letras e números");
        }
    }

    /**
     * Valida se duas datas estão em ordem correta (início <= fim)
     */
    public static void validateDateOrder(LocalDate startDate, LocalDate endDate, String startFieldName, String endFieldName) {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new BusinessException(startFieldName + " deve ser anterior ou igual a " + endFieldName);
        }
    }
} 