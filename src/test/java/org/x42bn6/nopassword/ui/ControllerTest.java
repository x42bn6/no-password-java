package org.x42bn6.nopassword.ui;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.x42bn6.nopassword.ApplicationSubService;
import org.x42bn6.nopassword.DomainSubService;
import org.x42bn6.nopassword.Salts;
import org.x42bn6.nopassword.Service;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class ControllerTest {

    @TempDir
    public Path temporaryDirectory;

    private Controller controller;
    private Model model;
    private NoPassword noPassword;

    @BeforeEach
    void setUp() {
        model = mock(Model.class);
        noPassword = mock(NoPassword.class);
        controller = new Controller(model, noPassword);
    }

    @Test
    void saveSalts() {
        Salts salts = new Salts();

        Service service = new Service("Steam");
        service.addSubService(new ApplicationSubService());
        service.addSubService(new DomainSubService("steampowered.com"));
        salts.getPasswordForService(service, "password".getBytes(StandardCharsets.UTF_8));
        salts.addService(service);

        when(model.getSalts()).thenReturn(salts);

        File outputFile = createOutputPath().toFile();
        when(model.getSaveFile()).thenReturn(outputFile);

        controller.saveSalts();

        assertTrue(outputFile.length() > 0);
    }

    @Test
    void loadSalts() {
        controller.loadSalts(new File(ControllerTest.class.getResource("/sample-salts.json").getFile()));

        verify(model).setSalts(any(Salts.class));
    }

    private Path createOutputPath() {
        return temporaryDirectory.resolve("temp.out");
    }
}