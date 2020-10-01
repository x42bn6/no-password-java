package org.x42bn6.nopassword;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.x42bn6.nopassword.hashingstrategies.HashOutput;
import org.x42bn6.nopassword.hashingstrategies.HashingStrategy;

import java.util.*;
import java.util.stream.Collectors;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;

/**
 * A {@code Salts} is a serializable representation of all {@link Service}s bound to their {@link CredentialMetadata}
 * instances.
 */
@JsonAutoDetect(fieldVisibility = ANY)
public class Salts {
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
        HashOutput hashOutput;
        boolean isNewSalt = existingSalt.newSalt;
        final HashingStrategy hashingStrategy = service.getHashingStrategy();
        if (isNewSalt) {
            hashOutput = hashingStrategy.generateHashWithNewSalt(unhashedPassword);
        } else {
            hashOutput = hashingStrategy.generateHashWithExistingSalt(existingSalt.salt, unhashedPassword);
        }

        if (isNewSalt) {
            bindServiceAndNewSalt(service, hashOutput.getSalt());
        }

        return hashOutput.getPassword();
    }

    private void bindServiceAndNewSalt(Service service, byte[] salt) {
        if (saltMap.containsKey(service.getName())) {
            throw new IllegalArgumentException("A service-salt combination already exists for service [" + service +
                    "]; cannot bind a new one");
        }

        CredentialMetadata credentialMetadata = new CredentialMetadata(salt, service);
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

    /**
     * Returns all of the data, stored as a map, mapping the service to the associated credential metadata.
     */
    @JsonIgnore
    public Map<Service, Collection<CredentialMetadata>> getData() {
        Map<Service, Collection<CredentialMetadata>> result = new HashMap<>();
        for (Map.Entry<String, Collection<CredentialMetadata>> entry : saltMap.entrySet()) {
            final Optional<Service> first = services.stream().filter((service) -> service.getName().equals(entry.getKey())).findFirst();
            if (first.isEmpty()) {
                throw new IllegalStateException("Unable to find corresponding service for service name " + entry.getKey());
            }

            result.put(first.get(), entry.getValue());
        }

        return result;
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
