module org.x42bn6.nopassword {
    requires javafx.graphics;
    requires javafx.controls;

    requires bcrypt;

    requires com.fasterxml.jackson.databind;

    opens org.x42bn6.nopassword to com.fasterxml.jackson.databind;

    opens org.x42bn6.nopassword.ui to javafx.graphics;

    exports org.x42bn6.nopassword;
}