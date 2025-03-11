import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Arrays;

public class PollutionDataManager {

    // Map: region -> (pollutant -> (year -> DataSet))
    private Map<String, Map<String, Map<String, DataSet>>> dataMap;
    
    public PollutionDataManager() {
        dataMap = new HashMap<>();
    }
    
    public void loadAllData() {
        // Define regions, pollutants, and years.
        String[] regions = {"London", "Manchester"};
        String[] pollutants = {"NO2", "PM10", "PM2.5"};
        String[] years = {"2018", "2019", "2020", "2021", "2022", "2023"};
        
        for (String region : regions) {
            dataMap.put(region, new HashMap<>());
            for (String pollutant : pollutants) {
                dataMap.get(region).put(pollutant, new HashMap<>());
                for (String year : years) {
                    String filePath;
                    if ("London".equals(region)) {
                        filePath = "UKAirPollutionData/" + pollutant + "_" + year + ".csv";
                    } else { // Manchester
                        filePath = "UKAirPollutionData/Manchester/" + pollutant + "_" + year + ".csv";
                    }
                    try {
                        DataLoader loader = new DataLoader();
                        DataSet ds = loader.loadDataSet(filePath);
                        dataMap.get(region).get(pollutant).put(year, ds);
                    } catch (Exception e) {
                        System.err.println("Error loading " + filePath + ": " + e.getMessage());
                    }
                }
            }
        }
    }
    
    public DataSet getDataSet(String pollutant, String year, String region) {
        if (!dataMap.containsKey(region))
            return null;
        if (!dataMap.get(region).containsKey(pollutant))
            return null;
        return dataMap.get(region).get(pollutant).get(year);
    }
    
    public double computeAverage(String pollutant, String year, String region) {
        DataSet ds = getDataSet(pollutant, year, region);
        if (ds == null || ds.getData().isEmpty())
            return 0.0;
        double sum = 0.0;
        int count = 0;
        for (DataPoint dp : ds.getData()) {
            sum += dp.value();
            count++;
        }
        return (count == 0) ? 0.0 : (sum / count);
    }
    
    public double computeMax(String pollutant, String year, String region) {
        DataSet ds = getDataSet(pollutant, year, region);
        if (ds == null || ds.getData().isEmpty())
            return 0.0;
        double maxVal = Double.MIN_VALUE;
        for (DataPoint dp : ds.getData()) {
            if (dp.value() > maxVal)
                maxVal = dp.value();
        }
        return maxVal;
    }
    
    public List<DataPoint> getMaxDataPoints(String pollutant, String year, String region) {
        DataSet ds = getDataSet(pollutant, year, region);
        if (ds == null)
            return Collections.emptyList();
        double maxVal = computeMax(pollutant, year, region);
        List<DataPoint> maxPoints = new ArrayList<>();
        for (DataPoint dp : ds.getData()) {
            if (Math.abs(dp.value() - maxVal) < 1e-9)
                maxPoints.add(dp);
        }
        return maxPoints;
    }
    
    public List<String> getAllYears() {
        return Arrays.asList("2018", "2019", "2020", "2021", "2022", "2023");
    }
}
