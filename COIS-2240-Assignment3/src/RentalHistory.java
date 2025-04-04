import java.util.List;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class RentalHistory {
    private List<RentalRecord> rentalRecords = new ArrayList<>();

    public void addRecord(RentalRecord record) {
        rentalRecords.add(record);
        saveRecord(record);
    }

    public List<RentalRecord> getRentalHistory() {
        return rentalRecords;
    }

    public List<RentalRecord> getRentalRecordsByCustomer(String customerName) {
        List<RentalRecord> result = new ArrayList<>();
        for (RentalRecord record : rentalRecords) {
            if (record.getCustomer().toString().toLowerCase().contains(customerName.toLowerCase())) {
                result.add(record);
            }
        }
        return result;
    }

    public List<RentalRecord> getRentalRecordsByVehicle(String licensePlate) {
        List<RentalRecord> result = new ArrayList<>();
        for (RentalRecord record : rentalRecords) {
            if (record.getVehicle().getLicensePlate().equalsIgnoreCase(licensePlate)) {
                result.add(record);
            }
        }
        return result;
    }
    
    //Task 1 Part 2
    private void saveRecord(RentalRecord record) {
        try (FileWriter writer = new FileWriter("rental_records.txt", true)) {
            writer.write(record.toString());
        } catch (IOException e) {
            System.err.println("Error saving rental record");
        }
    }
}