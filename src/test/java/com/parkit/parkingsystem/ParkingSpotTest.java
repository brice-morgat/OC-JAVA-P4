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
        //GIVEN
        ParkingSpot _parkingSpot = new ParkingSpot(4, ParkingType.CAR, false);
        parkingSpot = new ParkingSpot(4, ParkingType.CAR, false);
        //THEN
        assertTrue(parkingSpot.equals(_parkingSpot));
    }

    @Test
    void isEqualsSameParkingSpotTest() {
        //GIVEN
        parkingSpot = new ParkingSpot(4, ParkingType.CAR, false);
        //THEN
        assertTrue(parkingSpot.equals(parkingSpot));
    }

    @Test
    void isEqualsNullParkingSpotTest() {
        //GIVEN
        parkingSpot = new ParkingSpot(4, ParkingType.CAR, false);
        //THEN
        assertFalse(parkingSpot.equals(null));
    }

    @Test
    void isNotEqualsParkingSpotTest() {
        //GIVEN
        ParkingSpot _parkingSpot = new ParkingSpot(4, ParkingType.CAR, false);
        parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        //WHEN
        assertFalse(parkingSpot.equals(_parkingSpot));
    }

    @Test
    void isNotSameClassParkingSpotTest() {
        //GIVEN
        Ticket _parkingSpot = new Ticket();
        parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        //THEN
        assertFalse(parkingSpot.equals(_parkingSpot));
    }
}
