package org.x42bn6.nopassword;

import org.x42bn6.nopassword.hashingstrategies.HashingStrategy;

/**
 * A {@code Salt} represents the BCrypt salt associated with a {@link Service}.  A {@link CredentialMetadata} also
 * contains some metadata about the salt, such as the time at which it was generated, whether it is obsolete or not (as
 * a backup mechanism when generating new salts for password resets) or specific password requirements (such as maximum
 * length of the password).
 */
public class CredentialMetadata {
    /**
     * The hashing strategy associated with the credential (bcrypt/scrypt/argon2).
     */
    private final HashingStrategy hashingStrategy;

    /**
     * The BCrypt salt associated with the credential, in Base64 format (length 22, not 16).
     */
    private final byte[] salt;

    // Only used in serialization
    @SuppressWarnings("unused")
    private CredentialMetadata() {
        this(new byte[0], null);
    }

    public CredentialMetadata(byte[] salt, Service service) {
        this.salt = salt;
        if (null != service) {
            this.hashingStrategy = service.getHashingStrategy();
        } else {
            this.hashingStrategy = null;
        }
    }

    public byte[] getSalt() {
        return salt;
    }

    /**
     * Whether the credential is obsolete or not (default: false).
     */
    private boolean obsolete = false;

    public boolean isObsolete() {
        return obsolete;
    }

    public void setObsolete(boolean obsolete) {
        this.obsolete = obsolete;
    }
}
