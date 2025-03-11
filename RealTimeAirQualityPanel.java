import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RealTimeAirQualityPanel {
    
    private TextArea textArea;
    
    public Node getContent() {
        VBox root = new VBox(10);
        root.setPadding(new javafx.geometry.Insets(10));
        Button refreshButton = new Button("Refresh Real-Time Data");
        textArea = new TextArea();
        textArea.setEditable(false);
        refreshButton.setOnAction(e -> fetchData());
        root.getChildren().addAll(refreshButton, textArea);
        fetchData(); // initial load
        return root;
    }
    
    private void fetchData() {
        new Thread(() -> {
            try {
                // This endpoint typically returns code 200 with city data for Great Britain.
                URL url = new URL("https://api.openaq.org/v2/cities?country=GB&limit=5");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                int responseCode = con.getResponseCode();
                if (responseCode != 200) {
                    javafx.application.Platform.runLater(() -> {
                        textArea.setText("Error fetching real-time data. Response code: " + responseCode);
                    });
                    return;
                }
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine).append("\n");
                }
                in.close();
                con.disconnect();
                String result = content.toString();
                javafx.application.Platform.runLater(() -> {
                    textArea.setText(result);
                });
            } catch (Exception ex) {
                ex.printStackTrace();
                javafx.application.Platform.runLater(() -> {
                    textArea.setText("Error fetching real-time data: " + ex.getMessage());
                });
            }
        }).start();
    }
}
