package org.example.course.gui;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.course.Main;
import org.example.course.gui.listeners.DataChangeListener;
import org.example.course.gui.utils.Alerts;
import org.example.course.gui.utils.Utils;
import org.example.course.model.entities.Department;
import org.example.course.model.services.DepartmentService;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class DepartmentListController implements Initializable, DataChangeListener
{
    private DepartmentService departmentService;
    private ObservableList<Department> observableList;

    @FXML
    private TableView<Department> tableViewDepartment;
    @FXML
    private TableColumn<Department, Integer> tableColumnId;
    @FXML
    private TableColumn<Department, String> tableColumnName;
    @FXML
    private TableColumn<Department, Department> tableColumnEdit;
    @FXML
    private Button btNew;

    @FXML
    public void onBtNewAction(ActionEvent event)
    {
        Stage parentStage = Utils.currentStage(event);
        Department department = new Department();
        createDialogForm(department, "/org/example/course/DepartmentForm.fxml", parentStage);
    }

    public void setDepartmentService(DepartmentService departmentService)
    {
        this.departmentService = departmentService;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        initializeNodes();
    }

    private void initializeNodes()
    {
        tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
        tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));

        Stage stage = (Stage) Main.getMainScene().getWindow();
        tableViewDepartment.prefHeightProperty().bind(stage.heightProperty());
    }

    public void updateTableView()
    {
        if (departmentService == null)
        {
            throw new IllegalStateException("Service was null");
        }
        List<Department> list = departmentService.findAll();
        observableList = FXCollections.observableArrayList(list);
        tableViewDepartment.setItems(observableList);
        initEditButtons();
    }

    private void createDialogForm(Department department, String absoluteName, Stage parentStage)
    {
        try
        {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(absoluteName));
            Pane pane = fxmlLoader.load();

            DepartmentFormController controller = fxmlLoader.getController();
            controller.setDepartment(department);
            controller.setDepartmentService(new DepartmentService());
            controller.subscribeDataChangeListener(this);
            controller.updateFormData();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Enter Department data");
            dialogStage.setScene(new Scene(pane));
            dialogStage.setResizable(false);
            dialogStage.initOwner(parentStage);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.showAndWait();
        }
        catch (IOException e)
        {
            Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @Override
    public void onDataChanged()
    {
        updateTableView();
    }

    private void initEditButtons()
    {
        tableColumnEdit.setCellValueFactory(parameter -> new ReadOnlyObjectWrapper<>(parameter.getValue()));
        tableColumnEdit.setCellFactory(parameter -> new TableCell<Department, Department>()
        {
            private final Button button = new Button("Edit");

            @Override
            protected void updateItem(Department department, boolean empty)
            {
                super.updateItem(department, empty);
                if (department == null)
                {
                    setGraphic(null);
                    return;
                }
                setGraphic(button);
                button.setOnAction(event -> createDialogForm(department, "/org/example/course/DepartmentForm.fxml", Utils.currentStage(event)));
            }
        });
    }
}
