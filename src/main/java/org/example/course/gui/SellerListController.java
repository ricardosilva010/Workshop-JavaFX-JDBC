package org.example.course.gui;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.course.Main;
import org.example.course.db.DbIntegrityException;
import org.example.course.gui.listeners.DataChangeListener;
import org.example.course.gui.utils.Alerts;
import org.example.course.gui.utils.Utils;
import org.example.course.model.entities.Seller;
import org.example.course.model.services.SellerService;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class SellerListController implements Initializable, DataChangeListener
{
    private SellerService sellerService;
    private ObservableList<Seller> observableList;

    @FXML
    private TableView<Seller> tableViewSeller;
    @FXML
    private TableColumn<Seller, Integer> tableColumnId;
    @FXML
    private TableColumn<Seller, String> tableColumnName;
    @FXML
    private TableColumn<Seller, String> tableColumnEmail;
    @FXML
    private TableColumn<Seller, LocalDate> tableColumnBirthDate;
    @FXML
    private TableColumn<Seller, Double> tableColumnBaseSalary;
    @FXML
    private TableColumn<Seller, Seller> tableColumnEdit;
    @FXML
    private TableColumn<Seller, Seller> tableColumnRemove;
    @FXML
    private Button btNew;

    @FXML
    public void onBtNewAction(ActionEvent event)
    {
        Stage parentStage = Utils.currentStage(event);
        Seller seller = new Seller();
        createDialogForm(seller, "/org/example/course/SellerForm.fxml", parentStage);
    }

    public void setSellerService(SellerService sellerService)
    {
        this.sellerService = sellerService;
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
        tableColumnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        tableColumnBirthDate.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
        Utils.formatTableColumnDate(tableColumnBirthDate, "dd/MM/yyyy");
        tableColumnBaseSalary.setCellValueFactory(new PropertyValueFactory<>("baseSalary"));
        Utils.formatTableColumnDouble(tableColumnBaseSalary, 2);

        Stage stage = (Stage) Main.getMainScene().getWindow();
        tableViewSeller.prefHeightProperty().bind(stage.heightProperty());
    }

    public void updateTableView()
    {
        if (sellerService == null)
        {
            throw new IllegalStateException("Service was null");
        }
        List<Seller> list = sellerService.findAll();
        observableList = FXCollections.observableArrayList(list);
        tableViewSeller.setItems(observableList);
        initEditButtons();
        initRemoveButtons();
    }

    private void createDialogForm(Seller seller, String absoluteName, Stage parentStage)
    {
//        try
//        {
//            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(absoluteName));
//            Pane pane = fxmlLoader.load();
//
//            SellerFormController controller = fxmlLoader.getController();
//            controller.setSeller(seller);
//            controller.setSellerService(new SellerService());
//            controller.subscribeDataChangeListener(this);
//            controller.updateFormData();
//
//            Stage dialogStage = new Stage();
//            dialogStage.setTitle("Enter Seller data");
//            dialogStage.setScene(new Scene(pane));
//            dialogStage.setResizable(false);
//            dialogStage.initOwner(parentStage);
//            dialogStage.initModality(Modality.WINDOW_MODAL);
//            dialogStage.showAndWait();
//        }
//        catch (IOException e)
//        {
//            Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), Alert.AlertType.ERROR);
//        }
    }

    @Override
    public void onDataChanged()
    {
        updateTableView();
    }

    private void initEditButtons()
    {
        tableColumnEdit.setCellValueFactory(parameter -> new ReadOnlyObjectWrapper<>(parameter.getValue()));
        tableColumnEdit.setCellFactory(parameter -> new TableCell<Seller, Seller>()
        {
            private final Button button = new Button("Edit");

            @Override
            protected void updateItem(Seller seller, boolean empty)
            {
                super.updateItem(seller, empty);

                if (seller == null)
                {
                    setGraphic(null);
                    return;
                }
                setGraphic(button);
                button.setOnAction(event -> createDialogForm(seller, "/org/example/course/SellerForm.fxml", Utils.currentStage(event)));
            }
        });
    }

    private void initRemoveButtons()
    {
        tableColumnRemove.setCellValueFactory(parameter -> new ReadOnlyObjectWrapper<>(parameter.getValue()));
        tableColumnRemove.setCellFactory(parameter -> new TableCell<Seller, Seller>()
        {
            private final Button button = new Button("Remove");

            @Override
            protected void updateItem(Seller seller, boolean empty)
            {
                super.updateItem(seller, empty);

                if (seller == null)
                {
                    setGraphic(null);
                    return;
                }
                setGraphic(button);
                button.setOnAction(event -> removeEntity(seller));
            }
        });
    }

    private void removeEntity(Seller seller)
    {
        Optional<ButtonType> result = Alerts.showConfirmation("Confirmation", "Are you sure to delete?");

        if (result.get() == ButtonType.OK)
        {
            if (sellerService == null)
            {
                throw new IllegalStateException("Service was null");
            }
            try
            {
                sellerService.remove(seller);
                updateTableView();
            }
            catch (DbIntegrityException e)
            {
                Alerts.showAlert("Error removing object", null, e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }
}
