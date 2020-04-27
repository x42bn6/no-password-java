package org.x42bn6.nopassword;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.net.URI;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;

/**
 * A {@code ApplicationSubService} represents a {@link SubService} for an executable application.  It has an optional
 * {@link URI} to launch the application.
 */
@JsonAutoDetect(fieldVisibility = ANY, getterVisibility = NONE)
public class ApplicationSubService implements SubService {
    private URI uri;

    public ApplicationSubService() {

    }

    public ApplicationSubService(URI uri) {
        this.uri = uri;
    }

    @Override
    public String getDescription() {
        if (null == uri) {
            return "";
        }
        return uri.toString();
    }

}
