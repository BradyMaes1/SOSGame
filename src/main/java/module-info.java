module com.beginsecure.maventest.sosgamesprint2 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.beginsecure.maventest.sosgamesprint2 to javafx.fxml;
    exports com.beginsecure.maventest.sosgamesprint2;
}