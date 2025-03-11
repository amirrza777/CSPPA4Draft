import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.control.ComboBox;
import javafx.scene.control.CheckBox;

public class MapPanel {

    private PollutionDataManager dataManager;
    private ComboBox<String> pollutantCombo;
    private ComboBox<String> yearCombo;
    private ComboBox<String> regionCombo;
    private CheckBox highPollutionOnlyCheck;
    
    private Pane mapPane;
    private Image mapImage;
    private double minX, maxX, minY, maxY;
    
    public MapPanel(PollutionDataManager dataManager,
                    ComboBox<String> pollutantCombo,
                    ComboBox<String> yearCombo,
                    ComboBox<String> regionCombo,
                    CheckBox highPollutionOnlyCheck) {
        this.dataManager = dataManager;
        this.pollutantCombo = pollutantCombo;
        this.yearCombo = yearCombo;
        this.regionCombo = regionCombo;
        this.highPollutionOnlyCheck = highPollutionOnlyCheck;
    }
    
    public Node getContent() {
        mapPane = new Pane();
        refreshMap();
        return mapPane;
    }
    
    public void refresh() {
        refreshMap();
    }
    
    private void refreshMap() {
        if (mapPane == null)
            return;
        mapPane.getChildren().clear();
        
        String region = regionCombo.getValue();
        String imageName;
        if ("Manchester".equals(region)) {
            imageName = "Manchester.png";
            // Approximate Manchester boundaries (adjust as needed)
            minX = 360000; maxX = 390000; minY = 400000; maxY = 430000;
        } else {
            imageName = "London.png";
            minX = 510394; maxX = 553297; minY = 153594; maxY = 193350;
        }
        
        try {
            mapImage = new Image(getClass().getResource(imageName).toExternalForm());
        } catch(Exception e) {
            System.err.println("Image not found: " + imageName);
            return;
        }
        
        ImageView mapView = new ImageView(mapImage);
        mapView.setPreserveRatio(false); // stretch image to fill
        mapView.fitWidthProperty().bind(mapPane.widthProperty());
        mapView.fitHeightProperty().bind(mapPane.heightProperty());
        mapPane.getChildren().add(mapView);
        
        String pollutant = pollutantCombo.getValue();
        String year = yearCombo.getValue();
        if (pollutant == null || year == null || region == null)
            return;
        
        DataSet ds = dataManager.getDataSet(pollutant, year, region);
        if (ds == null)
            return;
        
        double imgWidth = mapPane.getWidth();
        double imgHeight = mapPane.getHeight();
        double highThreshold = getHighThreshold(pollutant);
        
        for (DataPoint dp : ds.getData()) {
            double x = dp.x();
            double y = dp.y();
            double value = dp.value();
            if (x < minX || x > maxX || y < minY || y > maxY)
                continue;
            if (highPollutionOnlyCheck.isSelected() && value < highThreshold)
                continue;
            Point2D point = toMapCoordinates(x, y, imgWidth, imgHeight);
            Circle marker = new Circle(point.getX(), point.getY(), 3);
            marker.setFill(getColorForValue(value, pollutant));
            marker.setStroke(Color.BLACK);
            marker.setStrokeWidth(0.2);
            Tooltip tooltip = new Tooltip("GridCode: " + dp.gridCode() + "\nValue: " + String.format("%.2f", value));
            Tooltip.install(marker, tooltip);
            mapPane.getChildren().add(marker);
        }
    }
    
    private Point2D toMapCoordinates(double easting, double northing, double mapWidth, double mapHeight) {
        double xRatio = (easting - minX) / (maxX - minX);
        double yRatio = (maxY - northing) / (maxY - minY);
        return new Point2D(xRatio * mapWidth, yRatio * mapHeight);
    }
    
    private Color getColorForValue(double value, String pollutant) {
        double maxVal = 100.0;
        if ("NO2".equals(pollutant))
            maxVal = 100.0;
        else if ("PM10".equals(pollutant))
            maxVal = 50.0;
        else if ("PM2.5".equals(pollutant))
            maxVal = 25.0;
        double ratio = Math.min(1.0, value / maxVal);
        return Color.color(ratio, 1.0 - ratio, 0.0);
    }
    
    private double getHighThreshold(String pollutant) {
        switch (pollutant) {
            case "NO2":   return 40.0;
            case "PM10":  return 25.0;
            case "PM2.5": return 15.0;
            default:      return 30.0;
        }
    }
}
