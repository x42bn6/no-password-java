package org.x42bn6.nopassword.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.x42bn6.nopassword.NoPasswordException;
import org.x42bn6.nopassword.Salts;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * {@code Controller} is the controller class (in MVC) binding the model ({@link Model}) and view ({@link NoPassword})
 * together and storing as a central class for UI logic.
 */
public class Controller {
    /**
     * The model.
     */
    private final Model model;

    /**
     * The view.
     */
    private final NoPassword noPassword;

    public Controller(Model model, NoPassword noPassword) {
        this.model = model;
        this.noPassword = noPassword;
    }

    /**
     * Loads the salts from the input file, and updates the UI.
     *
     * @param file The file
     * @return The salts loaded
     */
    public Salts loadSalts(File file) {
        ObjectMapper objectMapper = getObjectMapper();
        Salts salts;
        try {
            salts = objectMapper.readValue(Files.newBufferedReader(file.toPath()), Salts.class);
        } catch (IOException e) {
            throw new NoPasswordException("Got I/O exception while loading file " + file, e);
        }

        model.setSalts(salts);
        model.setSaveFile(file);
        return salts;
    }

    /**
     * Saves the salts to the save file.
     */
    public void saveSalts() {
        ObjectMapper objectMapper = getObjectMapper();
        File saveFile = model.getSaveFile();
        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(saveFile.toPath())) {
            objectMapper.writeValue(bufferedWriter, model.getSalts());
        } catch (IOException e) {
            throw new NoPasswordException("Got I/O exception while writing to file " + saveFile, e);
        }
    }

    private ObjectMapper getObjectMapper() {
        return new ObjectMapper();
    }
}
