import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class PollutionDataManagerTest {

    private PollutionDataManager manager;

    @Before
    public void setUp() {
        manager = new PollutionDataManager();
        manager.loadAllData();
    }

    @Test
    public void testDataLoading() {
        // For example, test that the NO2 2018 dataset for London is loaded.
        assertNotNull("NO2 2018 dataset should be loaded",
            manager.getDataSet("NO2", "2018", "London"));
    }

    @Test
    public void testComputeAverage() {
        double avg = manager.computeAverage("NO2", "2018", "London");
        // We can't know the exact average, but it should be non-negative.
        assertTrue("Average should be >= 0", avg >= 0);
    }

    @Test
    public void testComputeMax() {
        double maxVal = manager.computeMax("PM10", "2020", "London");
        assertTrue("Max should be >= 0", maxVal >= 0);
    }
    
    @Test
    public void testMaxDataPoints() {
        double maxVal = manager.computeMax("PM2.5", "2021", "London");
        assertTrue("Max PM2.5 for 2021 should be >= 0", maxVal >= 0);
        assertFalse("List of max points should not be empty",
            manager.getMaxDataPoints("PM2.5", "2021", "London").isEmpty());
    }
}
