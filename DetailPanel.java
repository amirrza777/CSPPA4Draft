import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class DetailPanel {

    private PollutionDataManager dataManager;
    private ComboBox<String> pollutantCombo;
    private ComboBox<String> yearCombo;
    private ComboBox<String> regionCombo;
    
    private TableView<DataPoint> table;
    private Label detailLabel;
    private BorderPane root;
    
    public DetailPanel(PollutionDataManager dataManager,
                       ComboBox<String> pollutantCombo,
                       ComboBox<String> yearCombo,
                       ComboBox<String> regionCombo) {
        this.dataManager = dataManager;
        this.pollutantCombo = pollutantCombo;
        this.yearCombo = yearCombo;
        this.regionCombo = regionCombo;
    }
    
    public Node getContent() {
        root = new BorderPane();
        root.setPadding(new Insets(10));
        
        table = new TableView<>();
        
        TableColumn<DataPoint, String> codeCol = new TableColumn<>("Grid Code");
        codeCol.setCellValueFactory(dp -> new javafx.beans.property.SimpleStringProperty(dp.getValue().gridCode() + ""));
        
        TableColumn<DataPoint, Number> xCol = new TableColumn<>("X (Easting)");
        xCol.setCellValueFactory(dp -> new javafx.beans.property.SimpleDoubleProperty(dp.getValue().x()));
        
        TableColumn<DataPoint, Number> yCol = new TableColumn<>("Y (Northing)");
        yCol.setCellValueFactory(dp -> new javafx.beans.property.SimpleDoubleProperty(dp.getValue().y()));
        
        TableColumn<DataPoint, Number> valCol = new TableColumn<>("Pollution Value");
        valCol.setCellValueFactory(dp -> new javafx.beans.property.SimpleDoubleProperty(dp.getValue().value()));
        
        table.getColumns().addAll(codeCol, xCol, yCol, valCol);
        
        detailLabel = new Label("Select a row to see details...");
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                detailLabel.setText("GridCode: " + newVal.gridCode() + "\n" +
                                    "X: " + newVal.x() + "\n" +
                                    "Y: " + newVal.y() + "\n" +
                                    "Value: " + newVal.value());
            }
        });
        
        VBox bottomBox = new VBox(10, detailLabel);
        bottomBox.setPadding(new Insets(10));
        
        root.setCenter(table);
        root.setBottom(bottomBox);
        
        refreshTable();
        return root;
    }
    
    public void refresh() {
        refreshTable();
    }
    
    private void refreshTable() {
        table.getItems().clear();
        String pollutant = pollutantCombo.getValue();
        String year = yearCombo.getValue();
        String region = regionCombo.getValue();
        if (pollutant == null || year == null || region == null) return;
        
        DataSet ds = dataManager.getDataSet(pollutant, year, region);
        if (ds != null)
            table.getItems().addAll(ds.getData());
        detailLabel.setText("Select a row to see details...");
    }
}
