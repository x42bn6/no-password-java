package org.x42bn6.nopassword.ui;

import org.x42bn6.nopassword.Salts;

import java.io.File;

/**
 * {@code Model} is the model (in MVC) class representing the program state.
 */
public class Model {
    /**
     * The file where saved salts are stored.
     */
    private File saveFile;

    /**
     * The current set of salts.
     */
    private Salts salts;

    public File getSaveFile() {
        return saveFile;
    }

    public void setSaveFile(File saveFile) {
        this.saveFile = saveFile;
    }

    public Salts getSalts() {
        return salts;
    }

    public void setSalts(Salts salts) {
        this.salts = salts;
    }
}
