module com.course {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.course to javafx.fxml;
    exports com.course;
}