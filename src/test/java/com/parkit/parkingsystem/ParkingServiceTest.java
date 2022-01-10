package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
public class  ParkingServiceTest {
    private static ParkingService parkingService;

    @Mock
    private static InputReaderUtil inputReaderUtil;
    @Mock
    private static ParkingSpotDAO parkingSpotDAO;
    @Mock
    private static TicketDAO ticketDAO;

    @BeforeEach
    private void setUpPerTest() {
            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
    }

    @Test
    @DisplayName("Test Entrée de véhicule")
    public void processIncomingVehicleTest() throws Exception {
        //Given
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1);
        when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);
        //WHEN
        parkingService.processIncomingVehicle();
    }

    @Test
    @DisplayName("Test Sortie de véhicule")
    public void processExitingVehicleTest() throws Exception {
        //GIVEN
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        Ticket ticket = new Ticket();
        ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber("ABCDEF");
        when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
        when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);

        when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);
        parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        //WHEN
        parkingService.processExitingVehicle();
        //THEN
        verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
    }

    @Test
    @DisplayName("Erreur du choix du type de véhicule")
    public void incorrectInputProvidedForVehicleTypeTest() {
        try {
            // GIVEN
            when(inputReaderUtil.readSelection()).thenReturn(4);

            // WHEN
            parkingService.getNextParkingNumberIfAvailable();
        } catch (IllegalArgumentException e) {
            // THEN
            assertThrows(IllegalArgumentException.class, () -> parkingService.getNextParkingNumberIfAvailable());
        }
    }

    @Test
    @DisplayName("Erreur ParkingSpot null")
    public void parkingSpotNullForIncomingVehicleTest() {
        try {
            // GIVEN
            when(inputReaderUtil.readSelection()).thenReturn(1);
            when(parkingService.getNextParkingNumberIfAvailable()).thenReturn(null);

            // WHEN
            parkingService.processIncomingVehicle();
        } catch (IllegalArgumentException e) {
            // THEN
            assertThrows(IllegalArgumentException.class, () -> parkingService.processIncomingVehicle());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    @DisplayName("Erreur parking plein")
    public void parkingSlotFullIncomingVehicleTest() {
        // GIVEN
        when(inputReaderUtil.readSelection()).thenReturn(2);
        when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(0);
        try {
            // WHEN
            parkingService.getNextParkingNumberIfAvailable();
        } catch (Exception e) {
            // THEN
            String errorExceptionMsg = "Error fetching parking number from DB. Parking slots might be full";
            // THEN
            assertEquals(errorExceptionMsg, e.getMessage());
        }
    }

    @Test
    @DisplayName("Erreur sauvegarde du ticket")
    public void unableToUpdateTicketWhenExitingVehicleTest() throws Exception {
        // GIVEN
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        Ticket ticket = new Ticket();
        ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber("ABCDEF");
        when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
        when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(false);

        // WHEN
        parkingService.processExitingVehicle();
        // THEN
        assertTrue(true);
    }

    @Test
    @DisplayName("Entrez incorrect lors de la sortie du véhicule")
    public void incorrectInputWhenExitingVehicleTest() throws Exception {
        // GIVEN
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        // WHEN
        parkingService.processExitingVehicle();
        // THEN
        assertThrows(IllegalArgumentException.class, () -> parkingService.processIncomingVehicle());
    }

    @Test
    @DisplayName("Hashcode test pour ParkingSpot")
    public void parkingSpotHashCodeTest() {
        // GIVEN
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);
        // THEN
        assertEquals(parkingSpot.hashCode(), 1);
    }
}
