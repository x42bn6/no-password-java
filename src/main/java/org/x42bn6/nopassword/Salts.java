package org.x42bn6.nopassword;

import at.favre.lib.crypto.bcrypt.BCrypt;
import at.favre.lib.crypto.bcrypt.Radix64Encoder;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.util.*;
import java.util.stream.Collectors;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;

/**
 * A {@code Salts} is a serializable representation of all {@link Service}s bound to their {@link CredentialMetadata}
 * instances.
 */
@JsonAutoDetect(fieldVisibility = ANY)
public class Salts {
    public static final int PREFIX_LENGTH = BCrypt.Version.VERSION_2A.versionIdentifier.length;
    public static final int COST = 6;
    public static final int COST_LENGTH_OCTAL = 2;
    public static final int HASHED_PASSWORD_LENGTH = 31;
    public static final int SEPARATOR_LENGTH = 1;
    public static final int SALT_LENGTH = 22;
    //                                           $                  2a              $                  06                  $
    public static final int LENGTH_BEFORE_SALT = SEPARATOR_LENGTH + PREFIX_LENGTH + SEPARATOR_LENGTH + COST_LENGTH_OCTAL + SEPARATOR_LENGTH;

    // Dependencies


    // Mutable state
    /**
     * The collection of {@link Service} IDs linked to their {@link CredentialMetadata} instances.
     * <p>
     * The name of the service, used as an ID, is used as a key.  This is to get around JSON complexities when
     * serializing complex keys.
     */
    private Map<String, Collection<CredentialMetadata>> saltMap = new HashMap<>();

    /**
     * The collection of {@link Service}s.
     */
    private Collection<Service> services = new ArrayList<>();

    /**
     * Adds a service to be recognised.
     *
     * @param service The new service
     */
    public void addService(Service service) {
        services.add(service);
    }

    /**
     * Generates a password for the specified {@link Service}, using a stored salt if one exists, or creating one and
     * storing it if not.
     *
     * @param service The service
     * @return The generated password
     */
    public byte[] getPasswordForService(Service service, byte[] unhashedPassword) {
        MaybeExistingSalt existingSalt = getExistingSalt(service);
        byte[] output;
        boolean isNewSalt = existingSalt.newSalt;
        if (isNewSalt) {
            output = generateHashWithNewSalt(unhashedPassword);
        } else {
            Radix64Encoder.Default radix64Encoder = new Radix64Encoder.Default();
            byte[] decodedSalt = radix64Encoder.decode(existingSalt.salt);
            output = generateHashWithExistingSalt(decodedSalt, unhashedPassword);
        }

        byte[] salt = new byte[SALT_LENGTH];
        byte[] hashedPassword = new byte[HASHED_PASSWORD_LENGTH];
        // Sample hash:
        // $2a$06$Xj6qWTDv.Jbhk8Z44PHolewAq4uwZrBjwUplueEk0ns1kstNsCWri
        // |A |B |C                    |D                             |
        // A = prefix, B = cost (0x), C = salt, D = password
        System.arraycopy(output, LENGTH_BEFORE_SALT, salt, 0, SALT_LENGTH);
        System.arraycopy(output, LENGTH_BEFORE_SALT + SALT_LENGTH, hashedPassword, 0, HASHED_PASSWORD_LENGTH);

        if (isNewSalt) {
            bindServiceAndNewSalt(service, salt);
        }

        return hashedPassword;
    }

    private byte[] generateHashWithNewSalt(byte[] unhashedPassword) {
        return BCrypt.withDefaults().hash(COST, unhashedPassword);
    }

    private byte[] generateHashWithExistingSalt(byte[] salt, byte[] unhashedPassword) {
        return BCrypt.withDefaults().hash(COST, salt, unhashedPassword);
    }

    private void bindServiceAndNewSalt(Service service, byte[] salt) {
        if (saltMap.containsKey(service.getName())) {
            throw new IllegalArgumentException("A service-salt combination already exists for service [" + service +
                    "]; cannot bind a new one");
        }

        int saltLength = salt.length;
        if (saltLength != SALT_LENGTH) {
            throw new IllegalArgumentException("Invalid salt length, expected " + SALT_LENGTH + " (Base64 encoding of" +
                    " 16 bytes), obtained " + saltLength);
        }

        CredentialMetadata credentialMetadata = new CredentialMetadata(salt);
        // Don't use singletonList for serialization (may want to add more later)
        Collection<CredentialMetadata> value = new ArrayList<>();
        value.add(credentialMetadata);
        saltMap.put(service.getName(), value);
    }

    private MaybeExistingSalt getExistingSalt(Service service) {
        final String key = service.getName();
        if (!saltMap.containsKey(key)) {
            return MaybeExistingSalt.newSalt();
        }

        Collection<CredentialMetadata> credentialMetadata = saltMap.get(key);
        List<CredentialMetadata> nonObsoleteMetadata =
                credentialMetadata.stream().filter(c -> !c.isObsolete()).collect(Collectors.toList());
        int size = nonObsoleteMetadata.size();
        if (size > 1) {
            throw new IllegalStateException("Did not find single non-obsolete metadata for service [" + service + "] " +
                    "(found " + size + ")");
        } else if (size == 0) {
            return MaybeExistingSalt.newSalt();
        } else {
            CredentialMetadata c = nonObsoleteMetadata.iterator().next();
            return MaybeExistingSalt.existingSalt(c.getSalt());
        }
    }

    private static final class MaybeExistingSalt {
        private final boolean newSalt;
        private final byte[] salt;

        public MaybeExistingSalt(boolean newSalt) {
            this.newSalt = newSalt;
            this.salt = new byte[0];
        }

        public MaybeExistingSalt(boolean newSalt, byte[] salt) {
            this.newSalt = newSalt;
            this.salt = salt;
        }

        public static MaybeExistingSalt existingSalt(byte[] salt) {
            return new MaybeExistingSalt(false, salt);
        }

        public static MaybeExistingSalt newSalt() {
            return new MaybeExistingSalt(true);
        }
    }
}
