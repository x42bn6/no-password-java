package org.x42bn6.nopassword.ui;

import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Collection;

/**
 * {@code NoPassword} is the entry point to the program.
 */
public class NoPassword extends javafx.application.Application {
    /**
     * Used to select salt files to load.
     */
    private final FileChooser fileChooser = new FileChooser();

    /**
     * The underlying model of the program.
     */
    private Model model;

    /**
     * The collection of {@link ServiceView}s - view wrappers on top of {@code Service}s.
     */
    private Collection<ServiceView> serviceViews = new ArrayList<>();

    @Override
    public void start(Stage primaryStage) {
        MenuBar menuBar = createMenuBar(primaryStage);

        model = new Model();

        VBox vBox = new VBox(menuBar);
        Scene scene = new Scene(vBox, 640, 480);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private MenuBar createMenuBar(Stage stage) {
        MenuBar menuBar = new MenuBar();

        Menu file = new Menu("File");
        MenuItem open = new MenuItem("Open salt file");
        open.setOnAction((e) -> {
            model.setSaveFile(fileChooser.showOpenDialog(stage));
            // Show salts
        });
        MenuItem settings = new MenuItem("Settings");
        MenuItem exit = new MenuItem("Exit");
        file.getItems().addAll(open, settings, exit);

        Menu help = new Menu("Help");
        MenuItem userGuide = new MenuItem("User guide");
        MenuItem about = new MenuItem("About");
        help.getItems().addAll(userGuide, about);

        menuBar.getMenus().addAll(file, help);
        return menuBar;
    }
}
