package org.x42bn6.nopassword;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.x42bn6.nopassword.hashingstrategies.HashingStrategy;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A {@code Service} represents a container of {@link SubService}s that have linked credentials.  For example, a user's
 * Steam credentials could have the following structure:
 *
 * <ol>
 *     <li>Service: Steam</li>
 *     <li>
 *         <ol>
 *             <li>Sub-service: Steam application ({@link NamedSubService})</li>
 *             <li>Sub-service: Steam 2FA application ({@link NamedSubService})</li>
 *             <li>Sub-service: steampowered.com ({@link DomainSubService})</li>
 *             <li>Sub-service: steamcommunity.com ({@link DomainSubService})</li>
 *         </ol>
 *     </li>
 * </ol>
 */
public class Service {
    /**
     * The name of the service.
     */
    private final String name;

    public String getName() {
        return name;
    }

    /**
     * The service's associated {@link SubService}s.
     */
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_ARRAY)
    private Collection<SubService> subServices = new ArrayList<>();

    public void addSubService(SubService subService) {
        subServices.add(subService);
    }

    public Collection<SubService> getSubServices() {
        return subServices;
    }

    /**
     * The hashing strategy to be used by the service.
     */
    private final HashingStrategy hashingStrategy;

    public HashingStrategy getHashingStrategy() {
        return hashingStrategy;
    }

    // Only used in serialization
    @SuppressWarnings("unused")
    private Service() {
        this(null, null);
    }

    public Service(String name, HashingStrategy hashingStrategy) {
        this.name = name;
        this.hashingStrategy = hashingStrategy;
    }
}
