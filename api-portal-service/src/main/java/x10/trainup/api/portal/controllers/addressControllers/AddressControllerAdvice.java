package x10.trainup.api.portal.controllers.addressControllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import x10.trainup.commons.response.ApiResponse;

import java.util.UUID;

@RestControllerAdvice(assignableTypes = AddressController.class)
public class AddressControllerAdvice {

    @ExceptionHandler(RuntimeException.class)
    public ApiResponse<Void> handleRuntimeException(
            RuntimeException ex,
            HttpServletRequest request) {

        return ApiResponse.of(
                HttpStatus.BAD_REQUEST.value(),
                "ADDRESS.ERROR",
                ex.getMessage(),
                null,
                request.getRequestURI(),
                UUID.randomUUID().toString()
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ApiResponse<Void> handleIllegalArgumentException(
            IllegalArgumentException ex,
            HttpServletRequest request) {

        return ApiResponse.of(
                HttpStatus.BAD_REQUEST.value(),
                "ADDRESS.INVALID_ARGUMENT",
                ex.getMessage(),
                null,
                request.getRequestURI(),
                UUID.randomUUID().toString()
        );
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleException(
            Exception ex,
            HttpServletRequest request) {

        return ApiResponse.of(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "ADDRESS.INTERNAL_ERROR",
                "Đã xảy ra lỗi hệ thống",
                null,
                request.getRequestURI(),
                UUID.randomUUID().toString()
        );
    }
}