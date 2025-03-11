public class FileLoadDemo {
    public static void main(String[] args) {
        DataLoader loader = new DataLoader();
        DataSet ds = loader.loadDataSet("UKAirPollutionData/NO2_2018.csv");
        System.out.println(ds);
    }
}
