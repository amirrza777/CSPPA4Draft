import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.control.ComboBox;
import java.util.List;

public class StatisticsPanel {

    private PollutionDataManager dataManager;
    private ComboBox<String> pollutantCombo;
    private ComboBox<String> yearCombo;
    private ComboBox<String> regionCombo;
    
    private Label avgLabel;
    private Label maxLabel;
    private Label maxLocationsLabel;
    private LineChart<String, Number> lineChart;
    private VBox root;
    
    public StatisticsPanel(PollutionDataManager dataManager,
                           ComboBox<String> pollutantCombo,
                           ComboBox<String> yearCombo,
                           ComboBox<String> regionCombo) {
        this.dataManager = dataManager;
        this.pollutantCombo = pollutantCombo;
        this.yearCombo = yearCombo;
        this.regionCombo = regionCombo;
    }
    
    public Node getContent() {
        root = new VBox(10);
        root.setPadding(new Insets(10));
        
        avgLabel = new Label("Average: ");
        maxLabel = new Label("Max Value: ");
        maxLocationsLabel = new Label("Max Location(s): ");
        
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Year");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Pollution Level");
        lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Multi-Year Trend (Average and Max)");
        
        updateStats();
        updateTrendChart();
        
        root.getChildren().addAll(avgLabel, maxLabel, maxLocationsLabel, lineChart);
        return root;
    }
    
    public void refresh() {
        updateStats();
        updateTrendChart();
    }
    
    private void updateStats() {
        String pollutant = pollutantCombo.getValue();
        String year = yearCombo.getValue();
        String region = regionCombo.getValue();
        if (pollutant == null || year == null || region == null) return;
        
        double avg = dataManager.computeAverage(pollutant, year, region);
        double maxVal = dataManager.computeMax(pollutant, year, region);
        List<DataPoint> maxPoints = dataManager.getMaxDataPoints(pollutant, year, region);
        
        avgLabel.setText(String.format("Average: %.2f", avg));
        maxLabel.setText(String.format("Max Value: %.2f", maxVal));
        
        StringBuilder sb = new StringBuilder("Max Location(s): ");
        for (DataPoint dp : maxPoints) {
            sb.append(dp.gridCode()).append(" (")
              .append(dp.x()).append(", ")
              .append(dp.y()).append("); ");
        }
        maxLocationsLabel.setText(sb.toString());
    }
    
    private void updateTrendChart() {
        lineChart.getData().clear();
        String pollutant = pollutantCombo.getValue();
        String region = regionCombo.getValue();
        if (pollutant == null || region == null) return;
        
        List<String> years = dataManager.getAllYears();
        XYChart.Series<String, Number> avgSeries = new XYChart.Series<>();
        avgSeries.setName("Average " + pollutant);
        XYChart.Series<String, Number> maxSeries = new XYChart.Series<>();
        maxSeries.setName("Max " + pollutant);
        
        for (String yr : years) {
            double avg = dataManager.computeAverage(pollutant, yr, region);
            double max = dataManager.computeMax(pollutant, yr, region);
            avgSeries.getData().add(new XYChart.Data<>(yr, avg));
            maxSeries.getData().add(new XYChart.Data<>(yr, max));
        }
        lineChart.getData().addAll(avgSeries, maxSeries);
    }
}
