import java.util.List;
import java.time.LocalDate;
import java.util.ArrayList;

import java.io.FileWriter;
import java.io.IOException;

import java.util.Scanner;
import java.io.File;

public class RentalSystem {
	private static RentalSystem instance;
	
    private List<Vehicle> vehicles = new ArrayList<>();
    private List<Customer> customers = new ArrayList<>();
    private RentalHistory rentalHistory = new RentalHistory();

    private RentalSystem() {
    	loadData();
    }
    
    
    public static RentalSystem getInstance() {
        if (instance == null) {
            instance = new RentalSystem();
        }
        return instance;
    }
    
    public boolean addVehicle(Vehicle vehicle) {
        if (findVehicleByPlate(vehicle.getLicensePlate()) != null) {
            System.out.println("Error: A vehicle with license plate " + vehicle.getLicensePlate() + " already exists.");
            return false;
        }
        
        vehicles.add(vehicle);
        saveVehicle(vehicle);
        System.out.println("Vehicle with license plate " + vehicle.getLicensePlate() + " added successfully.");
        return true;
    }

    public boolean addCustomer(Customer customer) {
        if (findCustomerById(customer.getCustomerId()) != null) {
            System.out.println("Error: A customer with ID " + customer.getCustomerId() + " already exists.");
            return false;
        }

        customers.add(customer);
        saveCustomer(customer);
        System.out.println("Customer with ID " + customer.getCustomerId() + " added successfully.");
        return true;
    }


    public boolean rentVehicle(Vehicle vehicle, Customer customer, LocalDate date, double amount) {
        if (vehicle.getStatus() == Vehicle.VehicleStatus.AVAILABLE) {
            vehicle.setStatus(Vehicle.VehicleStatus.RENTED);
            rentalHistory.addRecord(new RentalRecord(vehicle, customer, date, amount, "RENT"));
            System.out.println("Vehicle rented to " + customer.getCustomerName());
            return true;
        }
        else {
            System.out.println("Vehicle is not available for renting. \n");
            return false;
        }
    }

    public boolean returnVehicle(Vehicle vehicle, Customer customer, LocalDate date, double extraFees) {
        if (vehicle.getStatus() == Vehicle.VehicleStatus.RENTED) {
            vehicle.setStatus(Vehicle.VehicleStatus.AVAILABLE);
            rentalHistory.addRecord(new RentalRecord(vehicle, customer, date, extraFees, "RETURN"));
            System.out.println("Vehicle returned by " + customer.getCustomerName());
            return true;
        }
        else {
            System.out.println("Vehicle is not rented.\n");
            return false;
        }
    }    

    public void displayAvailableVehicles() {
    	System.out.println("|     Type         |\tPlate\t|\tMake\t|\tModel\t|\tYear\t|");
    	System.out.println("---------------------------------------------------------------------------------");
    	 
        for (Vehicle v : vehicles) {
            if (v.getStatus() == Vehicle.VehicleStatus.AVAILABLE) {
                System.out.println("|     " + (v instanceof Car ? "Car          " : "Motorcycle   ") + "|\t" + v.getLicensePlate() + "\t|\t" + v.getMake() + "\t|\t" + v.getModel() + "\t|\t" + v.getYear() + "\t|\t");
            }
        }
        System.out.println();
    }
    
    public void displayAllVehicles() {
        for (Vehicle v : vehicles) {
            System.out.println("  " + v.getInfo());
        }
    }

    public void displayAllCustomers() {
        for (Customer c : customers) {
            System.out.println("  " + c.toString());
        }
    }
    
    public void displayRentalHistory() {
        for (RentalRecord record : rentalHistory.getRentalHistory()) {
            System.out.println(record.toString());
        }
    }
    
    public Vehicle findVehicleByPlate(String plate) {
        for (Vehicle v : vehicles) {
            if (v.getLicensePlate().equalsIgnoreCase(plate)) {
                return v;
            }
        }
        return null;
    }
    
    public Customer findCustomerById(int id) {
        for (Customer c : customers)
            if (c.getCustomerId() == id)
                return c;
        return null;
    }

    public Customer findCustomerByName(String name) {
        for (Customer c : customers)
            if (c.getCustomerName().equalsIgnoreCase(name))
                return c;
        return null;
    }
    
    //Task 1 Part 2
    private void saveVehicle(Vehicle vehicle) {
    	try(FileWriter writer = new FileWriter("vehicles.txt", true)){
            writer.write(vehicle.getInfo() + "\n");
    	} catch (IOException e) {
    		System.err.println("Error saving vehicle\n");
    	}
    }
    
    private void saveCustomer(Customer customer) {
    	try(FileWriter writer = new FileWriter("customers.txt", true)){
    		writer.write(customer.toString() + "\n");
    	} catch (IOException e) {
    		System.err.println("Error saving customer\n");
    	}
    }
    
    //Task 1 Part 3
    private void loadData() {
        // Load vehicles
        try (Scanner scanner = new Scanner(new File("vehicles.txt"))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.startsWith("|")) {
                    String[] parts = line.split("\\|");

                    if (parts.length >= 6) {
                        String plate = parts[1].trim();
                        String make = parts[2].trim();
                        String model = parts[3].trim();
                        int year = Integer.parseInt(parts[4].trim());
                        Vehicle.VehicleStatus status = Vehicle.VehicleStatus.valueOf(parts[5].trim());

                        Vehicle vehicle = null;

                        // Determine vehicle type from line content
                        if (line.contains("Horsepower:") && line.contains("Turbo:")) {
                        	int numSeats = Integer.parseInt(line.split("Seats:")[1].trim());
                            int horsePower = Integer.parseInt(line.split("Horsepower:")[1].split("\\|")[0].trim());
                            boolean hasTurbo = line.contains("Turbo: Yes");
                            SportCar sportcar = new SportCar(make, model, year, numSeats, horsePower, hasTurbo);
                            sportcar.setLicensePlate(plate);
                            vehicle = sportcar;
                        }
                        else if (line.contains("Seats:")) {
                            int numSeats = Integer.parseInt(line.split("Seats:")[1].trim());
                            Car car = new Car(make, model, year, numSeats);
                            car.setLicensePlate(plate);  // Assign plate after instantiation
                            vehicle = car;
                        } else if (line.contains("Cargo Capacity:")) {
                            double cargo = Double.parseDouble(line.split("Cargo Capacity:")[1].trim());
                            Truck truck = new Truck(make, model, year, cargo);
                            truck.setLicensePlate(plate);
                            vehicle = truck;
                        }
                        else if (line.contains("Sidecar:")) {
                            boolean hasSidecar = line.contains("Sidecar: Yes");
                            Motorcycle motorcycle = new Motorcycle(make, model, year, hasSidecar);
                            motorcycle.setLicensePlate(plate);
                            vehicle = motorcycle;
                        }

                        if (vehicle != null) {
                            vehicle.setStatus(status);
                            vehicles.add(vehicle);
                        }
                    }
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error loading vehicles: " + e.getMessage());
        }

        // Load customers
        try (Scanner scanner = new Scanner(new File("customers.txt"))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.startsWith("Customer ID:")) {
                    String[] parts = line.split("\\|");
                    int id = Integer.parseInt(parts[0].replace("Customer ID:", "").trim());
                    String name = parts[1].replace("Name:", "").trim();
                    customers.add(new Customer(id, name));
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading customers: "  + e.getMessage());
        }

        // Load rental records
        try (Scanner scanner = new Scanner(new File("rental_records.txt"))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                String[] parts = line.split(",");
                if (parts.length == 5) {
                    String type = parts[0];
                    String plate = parts[1];
                    int customerId = Integer.parseInt(parts[2]);
                    LocalDate date = LocalDate.parse(parts[3]);
                    double amount = Double.parseDouble(parts[4]);

                    Vehicle vehicle = findVehicleByPlate(plate);
                    Customer customer = findCustomerById(customerId);

                    if (vehicle != null && customer != null) {
                        RentalRecord record = new RentalRecord(vehicle, customer, date, amount, type);
                        rentalHistory.addRecord(record);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading rental records:"  + e.getMessage());
        }
    }		
    
}