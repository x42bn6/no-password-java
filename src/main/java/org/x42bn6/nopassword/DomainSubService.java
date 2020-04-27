package org.x42bn6.nopassword;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;

/**
 * A {@code DomainSubService} represents a {@link SubService} for a domain, such as github.com.
 */
@JsonAutoDetect(fieldVisibility = ANY, getterVisibility = NONE)
public class DomainSubService implements SubService {
    private final String domain;

    // Only used in serialization
    @SuppressWarnings("unused")
    private DomainSubService() {
        this(null);
    }

    public DomainSubService(String domain) {
        this.domain = domain;
    }

    @Override
    public String getDescription() {
        return domain;
    }
}
