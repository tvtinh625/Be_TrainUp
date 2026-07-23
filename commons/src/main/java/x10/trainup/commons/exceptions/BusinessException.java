package x10.trainup.commons.exceptions;

import lombok.Getter;

import java.util.Collections;
import java.util.Map;

/**
 * Exception chuẩn cho business/domain.
 * Luôn chứa ErrorDescriptor để mapping ra ErrorResponse.
 */
@Getter
public class BusinessException extends RuntimeException {
    private final ErrorDescriptor descriptor;
    private final Map<String, Object> details;

    public BusinessException(ErrorDescriptor descriptor) {
        super(descriptor.defaultMessage());
        this.descriptor = descriptor;
        this.details = Collections.emptyMap();
    }

    public BusinessException(ErrorDescriptor descriptor, String message) {
        super(message != null ? message : descriptor.defaultMessage());
        this.descriptor = descriptor;
        this.details = Collections.emptyMap();
    }

    public BusinessException(ErrorDescriptor descriptor, String message, Map<String, Object> details) {
        super(message != null ? message : descriptor.defaultMessage());
        this.descriptor = descriptor;
        this.details = (details == null ? Collections.emptyMap() : details);
    }
}
