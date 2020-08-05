package org.x42bn6.nopassword.ui;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.x42bn6.nopassword.CredentialMetadata;
import org.x42bn6.nopassword.Salts;
import org.x42bn6.nopassword.Service;
import org.x42bn6.nopassword.SubService;

import java.io.File;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Map;

/**
 * {@code NoPassword} is the entry point to the program.
 */
public class NoPassword extends javafx.application.Application {
    public static final String FILE_TO_LOAD_PARAMETER = "file";
    /**
     * Used to select salt files to load.
     */
    private final FileChooser fileChooser = new FileChooser();

    /**
     * The controller (in MVC terms) of the UI.
     */
    private Controller controller;

    /**
     * The underlying model of the program.
     */
    private Model model;

    // UI components
    private GridPane saltsBox;

    @Override
    public void start(Stage primaryStage) {
        MenuBar menuBar = createMenuBar(primaryStage);
        HBox servicesBox = createServicesBox();
        saltsBox = createSaltsBox();

        model = new Model();
        controller = new Controller(model, this);

        VBox vBox = new VBox(menuBar, servicesBox, saltsBox);

        Scene scene = new Scene(vBox, 640, 480);
        scene.getStylesheets().add("style.css");

        primaryStage.setScene(scene);
        primaryStage.setTitle("No Password");
        primaryStage.show();

        final Map<String, String> namedParameters = getParameters().getNamed();
        if (namedParameters.containsKey(FILE_TO_LOAD_PARAMETER)) {
            loadSalts(Paths.get(namedParameters.get(FILE_TO_LOAD_PARAMETER)).toFile());
        }
    }

    private GridPane createSaltsBox() {
        GridPane saltsBox = new GridPane();
        final ColumnConstraints first = new ColumnConstraints();
        first.setPercentWidth(25);
        final ColumnConstraints second = new ColumnConstraints();
        second.setPercentWidth(75);
        saltsBox.getColumnConstraints().addAll(first, second);
        return saltsBox;
    }

    private HBox createServicesBox() {
        Button addService = new Button("Add service");
        Text searchLabel = new Text("Search:");
        TextField searchText = new TextField();
        Button searchButton = new Button("Search");

        final HBox servicesBox = new HBox(5, addService, searchLabel, searchText, searchButton);
        servicesBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(searchText, Priority.ALWAYS);
        return servicesBox;
    }

    private MenuBar createMenuBar(Stage stage) {
        MenuBar menuBar = new MenuBar();

        Menu file = new Menu("File");
        MenuItem open = new MenuItem("Open salt file");
        open.setOnAction((e) -> loadSalts(stage));
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

    private void loadSalts(Stage stage) {
        final File file = fileChooser.showOpenDialog(stage);
        loadSalts(file);
    }

    private void loadSalts(File file) {
        final Salts salts = controller.loadSalts(file);
        final Map<Service, Collection<CredentialMetadata>> data = salts.getData();

        int rowIndex = 0;
        for (Service service : data.keySet()) {
            VBox serviceGrid = new VBox();
            serviceGrid.getChildren().add(new Text(service.getName()));
            for (SubService subService : service.getSubServices()) {
                serviceGrid.getChildren().add(new Text(subService.getDescription()));
            }
            setOddEvenBackground(rowIndex, serviceGrid);
            saltsBox.add(serviceGrid, 0, rowIndex);

            PasswordField userPassword = new PasswordField();
            HBox.setHgrow(userPassword, Priority.ALWAYS);

            Button generatePassword = new Button("Generate password");

            Button settings = new Button("Settings");

            HBox buttons = new HBox(5, userPassword, generatePassword, settings);
            buttons.setAlignment(Pos.CENTER_LEFT);
            setOddEvenBackground(rowIndex, buttons);

            saltsBox.add(buttons, 1, rowIndex++);
        }
    }

    private void setOddEvenBackground(int rowIndex, Node component) {
        if (rowIndex % 2 == 0) {
            component.getStyleClass().add("even");
        } else {
            component.getStyleClass().add("odd");
        }
    }
}
