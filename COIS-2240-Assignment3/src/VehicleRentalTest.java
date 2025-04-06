import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

class VehicleRentalTest {

    private Vehicle vehicle;
    
    private Customer customer;
    private RentalSystem rentalSystem;
    
    @BeforeEach
    public void setUp() {
        rentalSystem = RentalSystem.getInstance();

        vehicle = new Car("Toyota", "Camry", 2020, 5);
        vehicle.setLicensePlate("ABC123");

        customer = new Customer(1, "John Doe");

        rentalSystem.addVehicle(vehicle);
        rentalSystem.addCustomer(customer);
    }

    @Test
    void testLicensePlateValidation() {
        vehicle = new Vehicle("Toyota", "Corolla", 2020) {};
        assertDoesNotThrow(() -> {
            vehicle.setLicensePlate("AAA100");
            assertEquals("AAA100", vehicle.getLicensePlate());
        });
        assertDoesNotThrow(() -> {
            vehicle.setLicensePlate("ABC567");
            assertEquals("ABC567", vehicle.getLicensePlate());
        });
        assertDoesNotThrow(() -> {
            vehicle.setLicensePlate("ZZZ999");
            assertEquals("ZZZ999", vehicle.getLicensePlate());
        });
        

        assertThrows(IllegalArgumentException.class, () -> vehicle.setLicensePlate(""));
        assertThrows(IllegalArgumentException.class, () -> vehicle.setLicensePlate(null));
        assertThrows(IllegalArgumentException.class, () -> vehicle.setLicensePlate("AAA1000"));
        assertThrows(IllegalArgumentException.class, () -> vehicle.setLicensePlate("ZZZ99"));
    }
    

    @Test
    public void testRentAndReturnVehicle() {
        assertEquals(Vehicle.VehicleStatus.AVAILABLE, vehicle.getStatus());

        LocalDate rentDate = LocalDate.now();
        double rentalAmount = 100.0;
        boolean rentSuccess = rentalSystem.rentVehicle(vehicle, customer, rentDate, rentalAmount);

        
        assertTrue(rentSuccess);
        assertEquals(Vehicle.VehicleStatus.RENTED, vehicle.getStatus());


        boolean rentAgainSuccess = rentalSystem.rentVehicle(vehicle, customer, rentDate, rentalAmount);
        assertFalse(rentAgainSuccess);


        LocalDate returnDate = LocalDate.now().plusDays(1);
        double extraFees = 20.0;
        boolean returnSuccess = rentalSystem.returnVehicle(vehicle, customer, returnDate, extraFees);

        assertTrue(returnSuccess);
        assertEquals(Vehicle.VehicleStatus.AVAILABLE, vehicle.getStatus());

        boolean returnAgainSuccess = rentalSystem.returnVehicle(vehicle, customer, returnDate, extraFees);
        assertFalse(returnAgainSuccess);
    }

    @Test
    public void testSingletonRentalSystem() {
        try {
            // Access the constructor of the RentalSystem class using reflection
            Constructor<RentalSystem> constructor = RentalSystem.class.getDeclaredConstructor();

            // Check if the constructor is private
            assertEquals(Modifier.PRIVATE, constructor.getModifiers(), "Constructor should be private");

            // Try to instantiate the RentalSystem using the Singleton method
            RentalSystem instance1 = RentalSystem.getInstance(); // Singleton instance

            // Create a new instance via reflection (this should succeed, but we want to check singleton behavior)
            constructor.setAccessible(true);
            RentalSystem instance2 = constructor.newInstance(); // Reflection-based instance

            // Assert that instance1 and instance2 are different, confirming reflection bypasses singleton
            assertNotSame(instance1, instance2, "Reflection should create a new instance.");

            // Validate that the getInstance() method is still giving the correct instance
            RentalSystem instance3 = RentalSystem.getInstance();

            // Assert that instance3 is the same as instance1, confirming Singleton behavior
            assertSame(instance1, instance3, "getInstance() should return the same instance every time.");
            
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }
}