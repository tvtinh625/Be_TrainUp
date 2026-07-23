package x10.trainup.mailbox.core.errors;

import x10.trainup.commons.exceptions.ErrorDescriptor;

public enum MailBoxError implements ErrorDescriptor {
    MAILBOX_NOT_FOUND(404,"MBX.NOT_FOUND","Mailbox not found"),
    INVALID_MAILBOX_NAME(400,"MBX.INVALID_NAME","Invalid mailbox name");

    private final int http;
    private final String code;
    private final String msg;
    MailBoxError(int http, String code, String msg)
    { this.http=http; this.code=code; this.msg=msg; }

    public int httpStatus(){ return http; }
    public String code(){ return code; }
    public String defaultMessage(){ return msg; }
}
