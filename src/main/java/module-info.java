module org.x42bn6.nopassword {
    requires javafx.graphics;
    requires javafx.controls;

    requires bcrypt;
    requires scrypt;
    requires org.bouncycastle.provider;
    requires spring.security.crypto;
    requires com.fasterxml.jackson.databind;

    opens org.x42bn6.nopassword to com.fasterxml.jackson.databind;
    opens org.x42bn6.nopassword.hashingstrategies to com.fasterxml.jackson.databind;

    opens org.x42bn6.nopassword.ui to javafx.graphics;

    exports org.x42bn6.nopassword;
    exports org.x42bn6.nopassword.hashingstrategies;
}