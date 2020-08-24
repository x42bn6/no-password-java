package org.x42bn6.nopassword.ui;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.x42bn6.nopassword.DomainSubService;
import org.x42bn6.nopassword.NamedSubService;
import org.x42bn6.nopassword.Salts;
import org.x42bn6.nopassword.Service;
import org.x42bn6.nopassword.hashingstrategies.Argon2HashingStrategy;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class ControllerTest {

    public static final byte[] UNHASHED_PASSWORD = "password".getBytes(StandardCharsets.UTF_8);
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

        Service steam = new Service("Steam", Argon2HashingStrategy.withDefaultParameters());
        steam.addSubService(new NamedSubService("Steam"));
        steam.addSubService(new DomainSubService("steampowered.com"));
        salts.getPasswordForService(steam, UNHASHED_PASSWORD);
        salts.addService(steam);

        Service google = new Service("Google", Argon2HashingStrategy.withDefaultParameters());
        google.addSubService(new NamedSubService("Google Authenticator"));
        google.addSubService(new DomainSubService("google.com"));
        google.addSubService(new DomainSubService("youtube.com"));
        salts.getPasswordForService(google, UNHASHED_PASSWORD);
        salts.addService(google);

        when(model.getSalts()).thenReturn(salts);

        File outputFile = createOutputPath().toFile();
        when(model.getSaveFile()).thenReturn(outputFile);

        controller.saveSalts();

        assertTrue(outputFile.length() > 0);
    }

    @Test
    void loadSalts() {
        final File file = new File(ControllerTest.class.getResource("/sample-salts.json").getFile());
        final Salts salts = controller.loadSalts(file);

        verify(model).setSalts(salts);
        verify(model).setSaveFile(file);
    }

    private Path createOutputPath() {
        return temporaryDirectory.resolve("temp.out");
    }
}