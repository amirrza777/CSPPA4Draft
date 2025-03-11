import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AirPollutionApp extends Application {

    private PollutionDataManager dataManager;
    
    // Shared controls
    private ComboBox<String> pollutantCombo;
    private ComboBox<String> yearCombo;
    private ComboBox<String> regionCombo;
    private CheckBox highPollutionOnlyCheck;
    
    // Panels
    private MapPanel mapPanel;
    private StatisticsPanel statsPanel;
    private DetailPanel detailPanel;
    private InteractiveMapPanel interactiveMapPanel;
    private RealTimeAirQualityPanel realTimePanel;

    @Override
    public void start(Stage primaryStage) {
        // Load all data (for London and Manchester)
        dataManager = new PollutionDataManager();
        dataManager.loadAllData();
        
        // Create shared controls.
        pollutantCombo = new ComboBox<>();
        pollutantCombo.getItems().addAll("NO2", "PM10", "PM2.5");
        pollutantCombo.setValue("NO2");

        yearCombo = new ComboBox<>();
        yearCombo.getItems().addAll("2018", "2019", "2020", "2021", "2022", "2023");
        yearCombo.setValue("2018");

        regionCombo = new ComboBox<>();
        regionCombo.getItems().addAll("London", "Manchester");
        regionCombo.setValue("London");

        highPollutionOnlyCheck = new CheckBox("Show High Pollution Only");
        highPollutionOnlyCheck.setSelected(false);

        // Create panels.
        WelcomePanel welcomePanel = new WelcomePanel();
        mapPanel = new MapPanel(dataManager, pollutantCombo, yearCombo, regionCombo, highPollutionOnlyCheck);
        statsPanel = new StatisticsPanel(dataManager, pollutantCombo, yearCombo, regionCombo);
        detailPanel = new DetailPanel(dataManager, pollutantCombo, yearCombo, regionCombo);
        interactiveMapPanel = new InteractiveMapPanel();
        realTimePanel = new RealTimeAirQualityPanel();

        // Create a TabPane and add tabs.
        TabPane tabPane = new TabPane();
        Tab welcomeTab = new Tab("Welcome", welcomePanel.getContent());
        welcomeTab.setClosable(false);
        Tab mapTab = new Tab("Map View", mapPanel.getContent());
        mapTab.setClosable(false);
        Tab statsTab = new Tab("Statistics", statsPanel.getContent());
        statsTab.setClosable(false);
        Tab detailTab = new Tab("Detailed Data", detailPanel.getContent());
        detailTab.setClosable(false);
        Tab interactiveTab = new Tab("Interactive Map", interactiveMapPanel.getContent());
        interactiveTab.setClosable(false);
        Tab realTimeTab = new Tab("RealTime Air Quality", realTimePanel.getContent());
        realTimeTab.setClosable(false);
        tabPane.getTabs().addAll(welcomeTab, mapTab, statsTab, detailTab, interactiveTab, realTimeTab);

        // Top bar for shared controls.
        VBox topBar = new VBox(5);
        topBar.setPadding(new Insets(10));
        Label controlsLabel = new Label("Select Pollutant, Year, and Region:");
        VBox controlsBox = new VBox(5, controlsLabel, pollutantCombo, yearCombo, regionCombo, highPollutionOnlyCheck);
        topBar.getChildren().add(controlsBox);
        
        BorderPane root = new BorderPane();
        root.setTop(topBar);
        root.setCenter(tabPane);
        
        // Centralized event handling.
        pollutantCombo.valueProperty().addListener((obs, oldVal, newVal) -> refreshPanels());
        yearCombo.valueProperty().addListener((obs, oldVal, newVal) -> refreshPanels());
        regionCombo.valueProperty().addListener((obs, oldVal, newVal) -> refreshPanels());
        highPollutionOnlyCheck.selectedProperty().addListener((obs, oldVal, newVal) -> mapPanel.refresh());
        
        Scene scene = new Scene(root, 1200, 800);
        primaryStage.setTitle("Air Pollution Viewer");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private void refreshPanels() {
        mapPanel.refresh();
        statsPanel.refresh();
        detailPanel.refresh();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
