package org.example.course.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.example.course.db.DbException;
import org.example.course.gui.utils.Alerts;
import org.example.course.gui.utils.Constraints;
import org.example.course.gui.utils.Utils;
import org.example.course.model.entities.Department;
import org.example.course.model.services.DepartmentService;

import java.net.URL;
import java.util.ResourceBundle;

public class DepartmentFormController implements Initializable
{
    private Department department;
    private DepartmentService departmentService;

    @FXML
    private TextField txtId;
    @FXML
    private TextField txtName;
    @FXML
    private Label labelErrorName;
    @FXML
    private Button btSave;
    @FXML
    private Button btCancel;

    public void setDepartment(Department department)
    {
        this.department = department;
    }

    public void setDepartmentService(DepartmentService departmentService)
    {
        this.departmentService = departmentService;
    }

    @FXML
    public void onBtSaveAction(ActionEvent event)
    {
        if (department == null)
        {
            throw new IllegalStateException("Entity was null");
        }
        if (departmentService == null)
        {
            throw new IllegalStateException("Service was null");
        }
        try
        {
            department = getFormData();
            departmentService.saveOrUpdate(department);
            Utils.currentStage(event).close();
        }
        catch (DbException e)
        {
            Alerts.showAlert("Error saving object", null, e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private Department getFormData()
    {
        Department department = new Department();

        department.setId(Utils.tryParseToInt(txtId.getText()));
        department.setName(txtName.getText());

        return department;
    }

    @FXML
    public void onBtCancelAction(ActionEvent event)
    {
        Utils.currentStage(event).close();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        initializeNodes();
    }

    private void initializeNodes()
    {
        Constraints.setTextFieldInteger(txtId);
        Constraints.setTextFieldMaxLength(txtName, 30);
    }

    public void updateFormData()
    {
        if (department == null)
        {
            throw new IllegalStateException("Entity was null");
        }
        txtId.setText(String.valueOf(department.getId()));
        txtName.setText(department.getName());
    }
}
