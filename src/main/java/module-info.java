module org.example.course {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.course to javafx.fxml;
    exports org.example.course;
}