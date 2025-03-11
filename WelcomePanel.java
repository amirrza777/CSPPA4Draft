import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class WelcomePanel {

    public WelcomePanel() {
        // No additional setup needed.
    }
    
    public Node getContent() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));
        
        Label title = new Label("Welcome to the Air Pollution Viewer");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        Label instructions = new Label(
            "Instructions:\n\n" +
            "1. Use the controls at the top to select the pollutant, year, and region.\n" +
            "2. The 'Map View' tab shows pollution data on the selected region's map.\n" +
            "3. The 'Statistics' tab displays multi-year trends (average and max) for the selected region.\n" +
            "4. The 'Detailed Data' tab lists all data points in a table.\n" +
            "5. The 'Interactive Map' tab provides an interactive OpenStreetMap view (embedded Leaflet).\n" +
            "6. The 'RealTime Air Quality' tab fetches live air quality data from OpenAQ.\n\n" +
            "If the Interactive Map is blank or real-time data fails, your environment may be blocking it.\n" +
            "Place 'London.png' and 'Manchester.png' in the same folder as your .java files.\n" +
            "Place CSVs in 'UKAirPollutionData' or 'UKAirPollutionData/Manchester'.\n\n" +
            "Enjoy exploring the data!"
        );
        
        root.getChildren().addAll(title, instructions);
        return root;
    }
}
