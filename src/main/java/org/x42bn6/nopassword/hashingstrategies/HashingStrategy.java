package org.x42bn6.nopassword.hashingstrategies;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * A {@code HashingStrategy} represents a cryptographic hashing strategy that is able to generate a hash with either a
 * new salt {@link #generateHashWithNewSalt(byte[])} or an existing salt {@link #generateHashWithExistingSalt(byte[],
 * byte[])}.
 * <p>
 * The output hashes are in the
 * <a href="https://passlib.readthedocs.io/en/stable/modular_crypt_format.html">Modular Crypt Format</a> or
 * <a href="https://github.com/P-H-C/phc-string-format/blob/master/phc-sf-spec.md">PHC</a> format.
 * <p>
 * The output is a wrapper object ({@link HashOutput}) containing the salt and hashed password.
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        property = "__type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = BcryptHashingStrategy.class, name = "bcryptHashingStrategy"),
        @JsonSubTypes.Type(value = ScryptHashingStrategy.class, name = "scryptHashingStrategy")
})
public interface HashingStrategy {
    /**
     * Generates a cryptographic hash with a new salt and input unhashed password.
     *
     * @param unhashedPassword The unhashed password
     * @return The cryptographic hash, in bytes
     */
    HashOutput generateHashWithNewSalt(byte[] unhashedPassword);

    HashOutput generateHashWithExistingSalt(byte[] salt, byte[] unhashedPassword);
}
