module org.example.course {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.course to javafx.fxml;
    exports org.example.course;
    exports org.example.course.gui;
    opens org.example.course.gui to javafx.fxml;
}