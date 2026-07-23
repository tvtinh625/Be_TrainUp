package x10.trainup.commons.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"status", "code", "message", "data", "path", "traceId", "timestamp"})
public final class ApiResponse<T> implements Serializable {

    private final int status;        // HTTP status
    private final String code;       // e.g. SUCCESS, AUTH.LOGIN_SUCCESS
    private final String message;    // Thông điệp cho FE
    private final T data;            // Payload

    private final String path;       // Request URI
    private final String traceId;    // For correlation
    private final Instant timestamp; // UTC

    public static <T> ApiResponse<T> of(int status, String code,
                                        String message, T data,
                                        String path, String traceId) {
        return ApiResponse.<T>builder()
                .status(status)
                .code(code == null ? "SUCCESS" : code)
                .message(message)
                .data(data)
                .path(path)
                .traceId(traceId)
                .timestamp(Instant.now())
                .build();
    }
}
