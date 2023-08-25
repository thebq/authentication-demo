package vn.vnpay.common.constant;

import org.ietf.jgss.GSSException;
import org.ietf.jgss.Oid;

/**
 * @author thebq
 * Created: 13/08/2023
 */
public class KerberosConstant {
    public final static Oid KRB5_OID;

    static {
        try {
            KRB5_OID = new Oid("1.2.840.113554.1.2.2");
        } catch (GSSException e) {
            throw new RuntimeException(e);
        }
    }
    public static final String URL = "url";
    public static final String USER_NAME = "username";
    public static final String PASS_WORD = "password";
    public static final String LOCAL_HOST = "localhost";
    public static final String KEYTAB_FILE = "service.keytab";
    public static final String LOGIN_URL = "/kerberos/login";
    public static final String TEST = "test";
    public static final String LAST_RUN = "lastRun";
    public static final String KERBEROS = "kerberos";
}