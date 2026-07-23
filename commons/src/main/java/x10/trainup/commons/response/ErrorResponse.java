package x10.trainup.commons.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "type", "title", "status", "code", "message",
        "detail", "path", "traceId", "timestamp"
})
public final class ErrorResponse implements Serializable {

    private final String type;       // VALIDATION | BUSINESS | SYSTEM
    private final String title;      // "Validation Error", "Conflict"
    private final Integer status;    // HTTP status code
    private final String code;       // e.g. USER.EMAIL_EXISTS
    private final String message;    // User-friendly message
    private final Object detail;     // Field errors / extra detail

    private final String path;       // Request URI
    private final String traceId;    // Trace/Correlation ID
    private final Instant timestamp; // Server UTC time

    /**
     * Universal factory method
     */
    public static ErrorResponse of(String type,
                                   int httpStatus,
                                   String code,
                                   String message,
                                   Object detail,
                                   String path,
                                   String traceId) {
        return ErrorResponse.builder()
                .type(type)
                .title(httpTitle(httpStatus))
                .status(httpStatus)
                .code(code)
                .message(message)
                .detail(detail == null ? Map.of() : detail)
                .path(path)
                .traceId(traceId)
                .timestamp(Instant.now())
                .build();
    }

    public static String httpTitle(int status) {
        return switch (status) {
            case 400 -> "Bad Request";
            case 401 -> "Unauthorized";
            case 403 -> "Forbidden";
            case 404 -> "Not Found";
            case 409 -> "Conflict";
            case 422 -> "Validation Error";
            case 429 -> "Too Many Requests";
            case 500 -> "Internal Server Error";
            default -> "Error";
        };
    }

    // Helper cho validation field error
    public static Map<String, List<String>> fieldError(String field, String message) {
        return Map.of(field, List.of(message));
    }

    // Helper build detail từ cặp key-value
    public static Map<String, Object> detailOf(Object... kv) {
        Map<String, Object> m = new LinkedHashMap<>();
        for (int i = 0; i + 1 < kv.length; i += 2) {
            m.put(String.valueOf(kv[i]), kv[i + 1]);
        }
        return m;
    }
}
