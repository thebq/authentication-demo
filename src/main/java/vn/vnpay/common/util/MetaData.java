package vn.vnpay.common.util;
/**
 * @author thebq
 * Created: 13/08/2023
 */
public enum MetaData {
    SUCCESS(200, "Success"),
    FAIL_LOGIN(303, "Fail to login"),
    NOT_FOUND(404, "Not found");
    private final Integer metaCode;
    private final String message;

    MetaData(int metaCode, String message) {
        this.metaCode = metaCode;
        this.message = message;
    }

    public Integer getMetaCode() {
        return metaCode;
    }

    public String getMessage() {
        return message;
    }
}
