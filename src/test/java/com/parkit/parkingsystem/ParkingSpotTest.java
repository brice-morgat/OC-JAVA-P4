package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ParkingSpotTest {
    ParkingSpot parkingSpot;

    @Test
    void isEqualsParkingSpotTest() {
        //Given
        ParkingSpot _parkingSpot = new ParkingSpot(4, ParkingType.CAR, false);
        parkingSpot = new ParkingSpot(4, ParkingType.CAR, false);

        assertTrue(parkingSpot.equals(_parkingSpot));
    }

    @Test
    void isNotEqualsParkingSpotTest() {
        //Given
        ParkingSpot _parkingSpot = new ParkingSpot(4, ParkingType.CAR, false);
        parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        assertFalse(parkingSpot.equals(_parkingSpot));
    }

    @Test
    void isNotSameClassParkingSpotTest() {
        //Given
        Ticket _parkingSpot = new Ticket();
        parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        assertFalse(parkingSpot.equals(_parkingSpot));
    }
}
