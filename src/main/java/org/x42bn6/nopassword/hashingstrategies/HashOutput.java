package org.x42bn6.nopassword.hashingstrategies;

/**
 * A {@code HashOutput} object is a wrapper object for the output of {@link HashingStrategy}.  It wraps the salt and the
 * hashed password.
 */
public class HashOutput {
    private final byte[] salt;
    private final byte[] password;

    public HashOutput(byte[] salt, byte[] password) {
        this.salt = salt;
        this.password = password;
    }

    public byte[] getSalt() {
        return salt;
    }

    public byte[] getPassword() {
        return password;
    }
}
