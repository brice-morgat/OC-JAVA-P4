package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FareCalculatorServiceTest {

    private static FareCalculatorService fareCalculatorService;
    private Ticket ticket;


    @BeforeAll
    private static void setUp() {
        fareCalculatorService = new FareCalculatorService();
    }

    @BeforeEach
    private void setUpPerTest() {
        ticket = new Ticket();
    }

    @Test
    @DisplayName("Test CalculateFareCar")
    public void calculateFareCar() {
        //GIVEN
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        //WHEN
        fareCalculatorService.calculateFare(ticket, false);
        //GIVEN
        assertEquals(ticket.getPrice(), Fare.CAR_RATE_PER_HOUR);
    }

    @Test
    @DisplayName("Test CalculateFareBike")
    public void calculateFareBike() {
        //GIVEN
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
        §§
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket, false);
        assertEquals(ticket.getPrice(), Fare.BIKE_RATE_PER_HOUR);
    }

    @Test
    @DisplayName("Test CalculateFareUnknownType")
    public void calculateFareUnknownType() {
        //Given
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.RIEN, false);
//        parkingSpot.setParkingType(null);
        Ticket mockedTicket = new Ticket();
        mockedTicket.setInTime(inTime);
        mockedTicket.setOutTime(outTime);
        mockedTicket.setParkingSpot(parkingSpot);
        //When
        assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(mockedTicket, false));
    }

    @Test
    @DisplayName("Test CalculateFareBikeWithFutureInTime")
    public void calculateFareBikeWithFutureInTime() {
        //GIVEN
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() + (60 * 60 * 1000));
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        //THEN
        assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket, false));
    }

    @Test
    @DisplayName("Test de calcul du prix d'un ticket vélo pour un temps passé inférieur à une heure")
    public void calculateFareBikeWithLessThanOneHourParkingTime() {
        //GIVEN
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (45 * 60 * 1000));//45 minutes parking time should give 3/4th parking fare
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket, false);
        //THEN
        assertEquals((0.75 * Fare.BIKE_RATE_PER_HOUR), ticket.getPrice());
    }

    @Test
    @DisplayName("Test de calcul du prix d'un ticket voiture pour un temps passé inférieur à une heure")
    public void calculateFareCarWithLessThanOneHourParkingTime() {
        //GIVEN
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (45 * 60 * 1000));//45 minutes parking time should give 3/4th parking fare
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket, false);
        //THEN
        assertEquals((0.75 * Fare.CAR_RATE_PER_HOUR), ticket.getPrice());
    }

    @Test
    @DisplayName("Test de calcul du prix d'un ticket voiture pour un temps passé supérieur à un jour")
    public void calculateFareCarWithMoreThanADayParkingTime() {
        //GIVEN
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (24 * 60 * 60 * 1000));//24 hours parking time should give 24 * parking fare per hour
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket, false);
        //THEN
        assertEquals((24 * Fare.CAR_RATE_PER_HOUR), ticket.getPrice());
    }

    @Test
    @DisplayName("Test de calcul de prix du ticket voiture pour un temps inférieur à 30 minutes")
    public void calculateFareCarWithLessThanHalfHourParkingTime() {
        //GIVEN
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (29 * 60 * 1000));//24 hours parking time should give 24 * parking fare per hour
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket, false);
        //THEN
        assertEquals((0 * Fare.CAR_RATE_PER_HOUR), ticket.getPrice());
    }

    @Test
    @DisplayName("Test de calcul de prix du ticket vélo pour un temps inférieur à 30 minutes")
    public void calculateFareBikeWithLessThanHalfHourParkingTime() {
        //GIVEN
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (29 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket, false);
        //THEN
        assertEquals( (0 * Fare.BIKE_RATE_PER_HOUR) , ticket.getPrice());
    }

    @Test
    @DisplayName("Test de calcul de prix du ticket voiture pour une heure avec une réduction de 5%")
    public void calculateFareCarWithFivePourcentDiscountWhenRecurring() {
        //GIVEN
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));//60 minutes parking time should give parking fare with 5% discount
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket, true);
        //THEN
        assertEquals(((1 * Fare.CAR_RATE_PER_HOUR) - ((1 * Fare.CAR_RATE_PER_HOUR) * 0.05)), ticket.getPrice());
    }

    @Test
    @DisplayName("Test de calcul de prix du ticket vélo pour une heure avec une réduction de 5%")
    public void calculateFareBikeWithFivePourcentDiscountWhenRecurring() {
        //GIVEN
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));//60 minutes parking time should give  parking fare
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket, true);
        //THEN
        assertEquals(((1 * Fare.BIKE_RATE_PER_HOUR) - ((1 * Fare.BIKE_RATE_PER_HOUR) * 0.05)), ticket.getPrice());
    }
}
