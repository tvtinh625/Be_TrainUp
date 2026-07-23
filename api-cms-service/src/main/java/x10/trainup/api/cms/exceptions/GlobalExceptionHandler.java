package x10.trainup.api.cms.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import x10.trainup.commons.exceptions.BusinessException;
import x10.trainup.commons.exceptions.CommonError;
import x10.trainup.commons.response.ErrorResponse;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final HttpServletRequest request;

    // -------------------- BUSINESS --------------------
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessException ex) {
        var d = ex.getDescriptor();
        var detail = ex.getDetails().isEmpty() ? Map.of() : ex.getDetails();

        warn("Business exception", d.code(), ex.getMessage(), detail);

        var body = ErrorResponse.of(
                "BUSINESS",
                d.httpStatus(),
                d.code(),
                ex.getMessage(),
                detail,
                path(),
                traceId()
        );
        return ResponseEntity.status(d.httpStatus()).body(body);
    }

    // -------------------- VALIDATION: @Valid DTO --------------------
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, List<String>> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.groupingBy(
                        fe -> fe.getField(),
                        LinkedHashMap::new,
                        Collectors.mapping(fe -> fe.getDefaultMessage(), Collectors.toList())
                ));

        warn("Validation error (DTO)", "VALIDATION_ERROR", "Request validation failed", fieldErrors);

        var body = ErrorResponse.of(
                "VALIDATION",
                422,
                "VALIDATION_ERROR",
                "Request validation failed",
                fieldErrors,
                path(),
                traceId()
        );
        return ResponseEntity.unprocessableEntity().body(body);
    }

    // -------------------- VALIDATION: @Validated param/path --------------------
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraint(ConstraintViolationException ex) {
        Map<String, List<String>> fieldErrors = ex.getConstraintViolations().stream()
                .collect(Collectors.groupingBy(
                        v -> v.getPropertyPath() == null ? "param" : v.getPropertyPath().toString(),
                        LinkedHashMap::new,
                        Collectors.mapping(v -> v.getMessage(), Collectors.toList())
                ));

        warn("Validation error (Constraint)", "VALIDATION_ERROR", "Request validation failed", fieldErrors);

        var body = ErrorResponse.of(
                "VALIDATION",
                422,
                "VALIDATION_ERROR",
                "Request validation failed",
                fieldErrors,
                path(),
                traceId()
        );
        return ResponseEntity.unprocessableEntity().body(body);
    }

    // -------------------- VALIDATION: Binding/Query/Form --------------------
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> handleBind(BindException ex) {
        Map<String, List<String>> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.groupingBy(
                        fe -> fe.getField(),
                        LinkedHashMap::new,
                        Collectors.mapping(fe -> fe.getDefaultMessage(), Collectors.toList())
                ));

        warn("Validation error (Bind)", "VALIDATION_ERROR", "Request validation failed", fieldErrors);

        var body = ErrorResponse.of(
                "VALIDATION",
                422,
                "VALIDATION_ERROR",
                "Request validation failed",
                fieldErrors,
                path(),
                traceId()
        );
        return ResponseEntity.unprocessableEntity().body(body);
    }

    // -------------------- REQUEST SHAPE/FORMAT --------------------
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadable(HttpMessageNotReadableException ex) {
        Map<String, List<String>> detail = ErrorResponse.fieldError("body", "Malformed JSON or unreadable request");

        warn("Request not readable", CommonError.REQUEST_INVALID.code(),
                "Request invalid format", detail);

        var body = ErrorResponse.of(
                "BUSINESS",
                HttpStatus.BAD_REQUEST.value(),
                CommonError.REQUEST_INVALID.code(),
                CommonError.REQUEST_INVALID.defaultMessage(),
                detail,
                path(),
                traceId()
        );
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParam(MissingServletRequestParameterException ex) {
        Map<String, List<String>> detail = ErrorResponse.fieldError(ex.getParameterName(), "Required parameter is missing");

        warn("Missing parameter", CommonError.REQUEST_INVALID.code(),
                "Missing required parameter", detail);

        var body = ErrorResponse.of(
                "BUSINESS",
                HttpStatus.BAD_REQUEST.value(),
                CommonError.REQUEST_INVALID.code(),
                CommonError.REQUEST_INVALID.defaultMessage(),
                detail,
                path(),
                traceId()
        );
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String expected = ex.getRequiredType() == null ? "unknown" : ex.getRequiredType().getSimpleName();
        Map<String, List<String>> detail = ErrorResponse.fieldError(ex.getName(), "Type mismatch, expected: " + expected);

        warn("Type mismatch", CommonError.REQUEST_INVALID.code(),
                "Argument type mismatch", detail);

        var body = ErrorResponse.of(
                "BUSINESS",
                HttpStatus.BAD_REQUEST.value(),
                CommonError.REQUEST_INVALID.code(),
                CommonError.REQUEST_INVALID.defaultMessage(),
                detail,
                path(),
                traceId()
        );
        return ResponseEntity.badRequest().body(body);
    }

    // -------------------- DATA/DB --------------------
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex) {
        warn("Data integrity violation", "DB.INTEGRITY", ex.getMostSpecificCause().getMessage(), Map.of());

        var body = ErrorResponse.of(
                "BUSINESS",
                HttpStatus.CONFLICT.value(),
                "DB.INTEGRITY",
                "Data integrity violation",
                Map.of(),
                path(),
                traceId()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    // -------------------- FALLBACK --------------------
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnknown(Exception ex) {
        log.error("Unhandled exception at {} traceId={}", path(), traceId(), ex);

        var body = ErrorResponse.of(
                "SYSTEM",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                CommonError.INTERNAL_ERROR.code(),
                CommonError.INTERNAL_ERROR.defaultMessage(),
                Map.of(),
                path(),
                traceId()
        );
        return ResponseEntity.internalServerError().body(body);
    }

    // -------------------- helpers --------------------
    private void warn(String what, String code, String message, Object detail) {
        log.warn("{} at {} traceId={} code={} message={} detail={}",
                what, path(), traceId(), code, message, detail);
    }

    private String path() { return request.getRequestURI(); }

    private String traceId() {
        var id = (String) request.getAttribute("traceId");
        if (id == null) id = MDC.get("traceId");
        return id;
    }
}
