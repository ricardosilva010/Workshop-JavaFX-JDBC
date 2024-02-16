package org.example.course.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.Callback;
import org.example.course.db.DbException;
import org.example.course.gui.listeners.DataChangeListener;
import org.example.course.gui.utils.Alerts;
import org.example.course.gui.utils.Constraints;
import org.example.course.gui.utils.Utils;
import org.example.course.model.entities.Department;
import org.example.course.model.entities.Seller;
import org.example.course.model.exceptions.ValidationException;
import org.example.course.model.services.DepartmentService;
import org.example.course.model.services.SellerService;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class SellerFormController implements Initializable
{
    private Seller seller;
    private SellerService sellerService;
    private DepartmentService departmentService;
    private ObservableList<Department> observableList;
    private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

    @FXML
    private TextField txtId;
    @FXML
    private TextField txtName;
    @FXML
    private TextField txtEmail;
    @FXML
    private DatePicker dpBirthDate;
    @FXML
    private TextField txtBaseSalary;
    @FXML
    private ComboBox<Department> comboBoxDepartment;
    @FXML
    private Label labelErrorName;
    @FXML
    private Label labelErrorEmail;
    @FXML
    private Label labelErrorBirthDate;
    @FXML
    private Label labelErrorBaseSalary;
    @FXML
    private Button btSave;
    @FXML
    private Button btCancel;

    public void setSeller(Seller Seller)
    {
        this.seller = Seller;
    }

    public void setServices(SellerService sellerService, DepartmentService departmentService)
    {
        this.sellerService = sellerService;
        this.departmentService = departmentService;
    }

    public void subscribeDataChangeListener(DataChangeListener listener)
    {
        dataChangeListeners.add(listener);
    }

    @FXML
    public void onBtSaveAction(ActionEvent event)
    {
        if (seller == null)
        {
            throw new IllegalStateException("Entity was null");
        }
        if (sellerService == null)
        {
            throw new IllegalStateException("Service was null");
        }
        try
        {
            seller = getFormData();
            sellerService.saveOrUpdate(seller);
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

    private Seller getFormData()
    {
        Seller seller = new Seller();

        ValidationException validationException = new ValidationException("Validation error");

        seller.setId(Utils.tryParseToInt(txtId.getText()));
        if (txtName.getText() == null || txtName.getText().trim().isEmpty())
        {
            validationException.addError("name", "Field can't be empty");
        }
        seller.setName(txtName.getText());

        if (txtEmail.getText() == null || txtEmail.getText().trim().isEmpty())
        {
            validationException.addError("email", "Field can't be empty");
        }
        seller.setEmail(txtEmail.getText());

        if (dpBirthDate.getValue() == null)
        {
            validationException.addError("birthDate", "Field can't be empty");
        }
        else
        {
            Instant instant = Instant.from(dpBirthDate.getValue().atStartOfDay(ZoneId.systemDefault()));
            seller.setBirthDate(LocalDate.from(instant.atZone(ZoneId.systemDefault())));
        }

        if (txtBaseSalary.getText() == null || txtBaseSalary.getText().trim().isEmpty())
        {
            validationException.addError("baseSalary", "Field can't be empty");
        }
        seller.setBaseSalary(Utils.tryParseToDouble(txtBaseSalary.getText()));

        seller.setDepartment(comboBoxDepartment.getValue());

        if (!validationException.getErrors().isEmpty())
        {
            throw validationException;
        }

        return seller;
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
        Constraints.setTextFieldMaxLength(txtName, 75);
        Constraints.setTextFieldMaxLength(txtEmail, 50);
        Utils.formatDatePicker(dpBirthDate, "dd/MM/yyyy");
        Constraints.setTextFieldDouble(txtBaseSalary);
        initializeComboBoxDepartment();
    }

    public void updateFormData()
    {
        if (seller == null)
        {
            throw new IllegalStateException("Entity was null");
        }
        txtId.setText(String.valueOf(seller.getId()));
        txtName.setText(seller.getName());
        txtEmail.setText(seller.getEmail());
        if (seller.getBirthDate() != null)
        {
            dpBirthDate.setValue(seller.getBirthDate());
        }
        Locale.setDefault(Locale.US);
        txtBaseSalary.setText(String.format("%.2f", seller.getBaseSalary()));
        if (seller.getDepartment() == null)
        {
            comboBoxDepartment.getSelectionModel().selectFirst();
        }
        else
        {
            comboBoxDepartment.setValue(seller.getDepartment());
        }
    }

    public void loadAssociatedObjects()
    {
        if (departmentService == null)
        {
            throw new IllegalStateException("DepartmentService was null");
        }
        List<Department> departmentList = departmentService.findAll();
        observableList = FXCollections.observableArrayList(departmentList);
        comboBoxDepartment.setItems(observableList);
    }

    private void setErrorMessages(Map<String, String> errors)
    {
        Set<String> fields = errors.keySet();

        labelErrorName.setText(fields.contains("name") ? errors.get("name") : "");
        labelErrorEmail.setText(fields.contains("email") ? errors.get("email") : "");
        labelErrorBirthDate.setText(fields.contains("birthDate") ? errors.get("birthDate") : "");
        labelErrorBaseSalary.setText(fields.contains("baseSalary") ? errors.get("baseSalary") : "");
    }

    private void initializeComboBoxDepartment()
    {
        Callback<ListView<Department>, ListCell<Department>> factory = lv -> new ListCell<Department>()
        {
            @Override
            protected void updateItem(Department item, boolean empty)
            {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getName());
            }
        };
        comboBoxDepartment.setCellFactory(factory);
        comboBoxDepartment.setButtonCell(factory.call(null));
    }
}
