package vn.vnpay.kerberos.kdc.authorization;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

public class NamePasswordCbHandler implements CallbackHandler {
    private final transient String name;
    private final transient char[] password;

    public NamePasswordCbHandler(String name, char[] password) {
        this.name = name;
        this.password = password;
    }

    public void handle(Callback[] callbacks) throws UnsupportedCallbackException {
        for (Callback cb : callbacks) {
            if (cb instanceof NameCallback) {
                NameCallback nc = (NameCallback) cb;
                nc.setName(name);
            } else if (cb instanceof PasswordCallback) {
                PasswordCallback pc = (PasswordCallback) cb;
                pc.setPassword(password);
            } else {
                throw new UnsupportedCallbackException(cb);
            }
        }
    }
}