package com.exemplo.bancoalcatteia.utils;

import com.exemplo.bancoalcatteia.exception.BusinessException;
import jakarta.persistence.EntityNotFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.regex.Pattern;

/**
 * Utilitários para validações consistentes em toda a aplicação
 */
public class ValidationUtils {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^\\(?\\d{2}\\)?\\s?\\d{4,5}-?\\d{4}$"
    );

    /**
     * Valida se um objeto não é nulo
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
            throw new BusinessException(fieldName + " é obrigatório e não pode estar vazio");
        }
    }

    /**
     * Valida se uma coleção não é nula ou vazia
     */
    public static void validateNotEmpty(Collection<?> collection, String fieldName) {
        if (collection == null || collection.isEmpty()) {
            throw new BusinessException(fieldName + " deve conter pelo menos um item");
        }
    }

    /**
     * Valida comprimento máximo de string
     */
    public static void validateMaxLength(String value, int maxLength, String fieldName) {
        if (value != null && value.length() > maxLength) {
            throw new BusinessException(fieldName + " não pode ter mais de " + maxLength + " caracteres");
        }
    }

    /**
     * Valida comprimento mínimo de string
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
        if (id == null || id <= 0) {
            throw new BusinessException(fieldName + " deve ser um número positivo");
        }
    }

    /**
     * Valida formato de email
     */
    public static void validateEmail(String email) {
        if (email != null && !EMAIL_PATTERN.matcher(email).matches()) {
            throw new BusinessException("Formato de email inválido");
        }
    }

    /**
     * Valida formato de telefone brasileiro
     */
    public static void validatePhone(String phone) {
        if (phone != null && !PHONE_PATTERN.matcher(phone).matches()) {
            throw new BusinessException("Formato de telefone inválido. Use: (11) 99999-9999 ou 11999999999");
        }
    }

    /**
     * Valida se uma data não é futura
     */
    public static void validateNotFutureDate(LocalDate date, String fieldName) {
        if (date != null && date.isAfter(LocalDate.now())) {
            throw new BusinessException(fieldName + " não pode ser uma data futura");
        }
    }

    /**
     * Valida se uma data não é muito antiga
     */
    public static void validateNotTooOldDate(LocalDate date, int maxYearsAgo, String fieldName) {
        if (date != null && date.isBefore(LocalDate.now().minusYears(maxYearsAgo))) {
            throw new BusinessException(fieldName + " não pode ser anterior a " + maxYearsAgo + " anos");
        }
    }

    /**
     * Valida período de datas
     */
    public static void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new BusinessException("Data de início não pode ser posterior à data de fim");
        }
    }

    /**
     * Valida se um valor está dentro de um range
     */
    public static void validateRange(Integer value, int min, int max, String fieldName) {
        if (value != null && (value < min || value > max)) {
            throw new BusinessException(fieldName + " deve estar entre " + min + " e " + max);
        }
    }

    /**
     * Valida se um valor está dentro de um range (double)
     */
    public static void validateRange(Double value, double min, double max, String fieldName) {
        if (value != null && (value < min || value > max)) {
            throw new BusinessException(fieldName + " deve estar entre " + min + " e " + max);
        }
    }

    /**
     * Valida se uma string contém apenas caracteres permitidos
     */
    public static void validateAlphanumeric(String value, String fieldName) {
        if (value != null && !value.matches("^[a-zA-Z0-9\\s]+$")) {
            throw new BusinessException(fieldName + " deve conter apenas letras, números e espaços");
        }
    }

    /**
     * Valida se uma cor está em formato hexadecimal
     */
    public static void validateHexColor(String color) {
        if (color != null && !color.matches("^#[0-9A-Fa-f]{6}$")) {
            throw new BusinessException("Cor deve estar no formato hexadecimal (#FFFFFF)");
        }
    }

    /**
     * Valida se uma entidade existe (não nula)
     */
    public static <T> T validateEntityExists(T entity, String entityName) {
        if (entity == null) {
            throw new EntityNotFoundException(entityName + " não encontrado");
        }
        return entity;
    }

    /**
     * Valida se uma entidade existe (não nula) com ID específico
     */
    public static <T> T validateEntityExists(T entity, String entityName, Integer id) {
        if (entity == null) {
            throw new EntityNotFoundException(entityName + " com ID " + id + " não encontrado");
        }
        return entity;
    }

    /**
     * Valida período máximo entre duas datas
     */
    public static void validateMaxDateRange(LocalDate startDate, LocalDate endDate, int maxDays, String fieldName) {
        if (startDate != null && endDate != null) {
            long daysBetween = endDate.toEpochDay() - startDate.toEpochDay();
            if (daysBetween > maxDays) {
                throw new BusinessException(fieldName + " não pode exceder " + maxDays + " dias");
            }
        }
    }

    /**
     * Valida se uma string representa um número
     */
    public static void validateNumeric(String value, String fieldName) {
        if (value != null && !value.matches("^\\d+$")) {
            throw new BusinessException(fieldName + " deve conter apenas números");
        }
    }

    /**
     * Valida se um valor enum é válido
     */
    public static <T extends Enum<T>> void validateEnum(String value, Class<T> enumClass, String fieldName) {
        if (value != null) {
            try {
                Enum.valueOf(enumClass, value);
            } catch (IllegalArgumentException e) {
                throw new BusinessException(fieldName + " deve ser um valor válido: " + 
                    java.util.Arrays.toString(enumClass.getEnumConstants()));
            }
        }
    }
} 