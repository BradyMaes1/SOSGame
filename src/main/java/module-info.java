module com.beginsecure.maventest.sosgamesprint2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires org.json; // Add this line


    opens com.beginsecure.maventest.sosgamesprint2 to javafx.fxml;
    exports com.beginsecure.maventest.sosgamesprint2;
}