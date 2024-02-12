package org.example.course.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.example.course.db.DbException;
import org.example.course.gui.listeners.DataChangeListener;
import org.example.course.gui.utils.Alerts;
import org.example.course.gui.utils.Constraints;
import org.example.course.gui.utils.Utils;
import org.example.course.model.entities.Department;
import org.example.course.model.exceptions.ValidationException;
import org.example.course.model.services.DepartmentService;

import java.net.URL;
import java.util.*;

public class DepartmentFormController implements Initializable
{
    private Department department;
    private DepartmentService departmentService;
    private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

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

    public void subscribeDataChangeListener(DataChangeListener listener)
    {
        dataChangeListeners.add(listener);
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
            notifyDataChangeListeners();
            Utils.currentStage(event).close();
        }
        catch (DbException e)
        {
            Alerts.showAlert("Error saving object", null, e.getMessage(), Alert.AlertType.ERROR);
        }
        catch (ValidationException e)
        {
            setErrorMessages(e.getErrors());
        }
    }

    private Department getFormData()
    {
        Department department = new Department();

        ValidationException validationException = new ValidationException("Validation error");

        department.setId(Utils.tryParseToInt(txtId.getText()));
        if (txtName.getText() == null || txtName.getText().trim().isEmpty())
        {
            validationException.addError("name", "Field can't be empty");
        }
        department.setName(txtName.getText());

        if (!validationException.getErrors().isEmpty())
        {
            throw validationException;
        }

        return department;
    }

    private void notifyDataChangeListeners()
    {
        for (DataChangeListener listener : dataChangeListeners)
        {
            listener.onDataChanged();
        }
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

    private void setErrorMessages(Map<String, String> errors)
    {
        Set<String> fields = errors.keySet();

        if (fields.contains("name"))
        {
            labelErrorName.setText(errors.get("name"));
        }
    }
}
