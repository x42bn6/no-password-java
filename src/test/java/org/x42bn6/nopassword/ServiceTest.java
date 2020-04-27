package org.x42bn6.nopassword;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class ServiceTest {

    private Service service;

    @BeforeEach
    void setUp() {
        service = new Service("name");
    }

    @Test
    void addSubService() {
        SubService subService = mock(SubService.class);

        service.addSubService(subService);

        Collection<SubService> result = service.getSubServices();
        assertEquals(1, result.size());
        assertEquals(subService, result.iterator().next());
    }
}