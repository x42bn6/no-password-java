package org.x42bn6.nopassword;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        property = "__type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = NamedSubService.class, name = "namedSubService"),
        @JsonSubTypes.Type(value = DomainSubService.class, name = "domainSubService")
})
public interface SubService {
    String getDescription();
}
