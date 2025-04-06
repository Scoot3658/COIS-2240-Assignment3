import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class VehicleRentalTest {

    private Vehicle vehicle;

    @BeforeEach
    void setUp() {
        vehicle = new Vehicle("Toyota", "Corolla", 2020) {};
    }

    @Test
    void testLicensePlateValidation() {
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
}