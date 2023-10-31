import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DataStore {
    private String timeDataFile = "timeData.txt";
    private String winDataFile = "winData.txt";
    private HashMap<Long, String> winDataStore;
    private HashMap<Long, Double> timeDataStore;

    public DataStore() {
        winDataStore = new HashMap<>();
        timeDataStore = new HashMap<>();
        loadTimeData();
        loadWinData();
    }

    public void putTime(long key, double val) {
        timeDataStore.put(key, val);
        saveTimeData();
    }

    public void putWin(long key, String val) {
        winDataStore.put(key, val);
        saveWinData();
    }

    public void delTime(long key) {
        timeDataStore.remove(key);
        saveTimeData();
    }

    public void delWin(long key) {
        winDataStore.remove(key);
        saveWinData();
    }

    public Double getTime(long key) {
        return timeDataStore.get(key);
    }

    public String getWin(long key) {
        return winDataStore.get(key);
    }

    public boolean timeKeyExists(long key) {
        return timeDataStore.containsKey(key);
    }

    public boolean winKeyExists(long key) {
        return winDataStore.containsKey(key);
    }

    public long findNextTimeKey() {
        long key = 0;
        while(timeKeyExists(key)) {
            key++;
        }
        return key;
    }

    public long findNextWinKey() {
        long key = 0;
        while(winKeyExists(key)) {
            key++;
        }
        return key;
    }

    public double averageRuntime() {
        double counter = 0;
        double timeTotal = 0;

        for(Map.Entry<Long, Double> entry : timeDataStore.entrySet()) {
            counter++;
            timeTotal += entry.getValue();
        }

        return (timeTotal/counter);
    }

    public float immuneWinPercentage() {
        float immuneCount = 0;
        float total = 0;

        for(Map.Entry<Long, String> entry : winDataStore.entrySet()) {
            if(Objects.equals(entry.getValue(), "Immune")) {
                immuneCount++;
            }
            total++;
        }

        return (immuneCount/total)*100;
    }

    public float infectedWinPercentage() {
        float infectedCount = 0;
        float total = 0;

        for(Map.Entry<Long, String> entry : winDataStore.entrySet()) {
            if(Objects.equals(entry.getValue(), "Infected")) {
                infectedCount++;
            }
            total++;
        }

        return  (infectedCount/total)*100;
    }
    private void loadTimeData() {
        try(BufferedReader reader = new BufferedReader(new FileReader(timeDataFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(":");
                if(data.length == 2) {
                    timeDataStore.put(Long.valueOf(data[0]), Double.valueOf(data[1]));
                    saveTimeData();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadWinData() {
        try(BufferedReader reader = new BufferedReader(new FileReader(winDataFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(":");
                if(data.length == 2) {
                    winDataStore.put(Long.valueOf(data[0]), data[1]);
                    saveWinData();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

     private void saveTimeData() {
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(this.timeDataFile))) {
            for(Map.Entry<Long, Double> entry : timeDataStore.entrySet()) {
                writer.write(entry.getKey() + ":" + entry.getValue());
                writer.newLine();
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private void saveWinData() {
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(winDataFile))) {
            for(Map.Entry<Long, String> entry : winDataStore.entrySet()) {
                writer.write(entry.getKey() + ":" + entry.getValue());
                writer.newLine();
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void wipeData() {
            long key = 0;
        while(winKeyExists(key)) {
            delWin(key);
            delTime(key);
            key++;
        }

        System.out.println("----------DATA HAS BEEN WIPED----------");
    }

    public void printDataStore() {
        System.out.println("-----Win Data Store-----");
        System.out.println(winDataStore);
        System.out.println("-----Time Data Store-----");
        System.out.println(timeDataStore);
        System.out.println("-------------------------");
    }

    public static void main(String[] args) {
    }
}
