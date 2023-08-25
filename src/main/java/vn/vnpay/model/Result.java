package vn.vnpay.model;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.common.util.MetaData;

/**
 * @author thebq
 * Created: 13/08/2023
 */
@Getter
@Setter
public class Result {
    private String code;
    private String message;
    private Object data;

    public Result(MetaData metaData, Object data) {
        this.code = String.valueOf(metaData.getMetaCode());
        this.message = metaData.getMessage();
        this.data = data;
    }

    @Override
    public String toString() {
        return String.format("{" +
                "\"code\":\"%s\"," +
                "\"message\":\"%s\"," +
                "\"data\":%s}", code, message, data);
    }
}
