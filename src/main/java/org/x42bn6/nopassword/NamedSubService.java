package org.x42bn6.nopassword;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;

/**
 * A {@code NamedSubService} represents a general {@link SubService} with a name.  It should be used for applications.
 */
@JsonAutoDetect(fieldVisibility = ANY, getterVisibility = NONE)
public class NamedSubService implements SubService {
    private final String name;

    private NamedSubService() {
        this(null);
    }

    public NamedSubService(String name) {
        this.name = name;
    }

    @Override
    public String getDescription() {
        return name;
    }

}
