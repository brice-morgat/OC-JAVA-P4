package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {
    private static String vehiculeRegNumber = "ABCDEFG";

    private static DataBasePrepareService dataBasePrepareService;
    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;

    @Mock
    private static InputReaderUtil inputReaderUtil;

    @BeforeAll
    private static void setUp() throws Exception{
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
    }

    @BeforeEach
    private void setUpPerTest() throws Exception {
        dataBasePrepareService.clearDataBaseEntries();
    }

//    @AfterAll
//    private static void tearDown(){
//        dataBasePrepareService.clearDataBaseEntries();
//    }

    /**
     * Test d'entrée d'un véhicule
     * Vérification en base de donnée de la bonne sauvegarde des informations
     */
    @Test
    public void testParkingACar() throws Exception {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(vehiculeRegNumber);
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

        parkingService.processIncomingVehicle();
        //TODO: check that a ticket is actually saved in DB and Parking table is updated with availability
        boolean isTicket = false;
        boolean isVehicule = false;
        Connection db = null;
        try {
            db = dataBaseTestConfig.getConnection();
            ResultSet ticketRes = db.prepareStatement("SELECT * from ticket WHERE VEHICLE_REG_NUMBER = \""+ vehiculeRegNumber+"\"").executeQuery();

            if (ticketRes.next()) {
                isTicket = true;
                ResultSet parkingRes = db.prepareStatement("SELECT * FROM parking WHERE PARKING_NUMBER = "+ ticketRes.getInt(2)).executeQuery();
                if(parkingRes.next() && !isVehicule) {
                    isVehicule = (parkingRes.getInt(2) == 0);
                }
                dataBaseTestConfig.closeResultSet(parkingRes);
            }
            dataBaseTestConfig.closeResultSet(ticketRes);
            assertTrue(isTicket && isVehicule);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        assertTrue(isTicket && isVehicule);

    }

    /**
     * Test de sortie d'un véhicule
     * Vérification en base de donnée de la bonne mise à jour des informations
     */
    @Test
    public void testParkingLotExit() throws Exception {
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        testParkingACar();
        Ticket ticket = ticketDAO.getTicket(vehiculeRegNumber);
        long outTIme = (ticket.getInTime().getTime() + (60 * 60 * 1000));
        parkingService.processExitingVehicle(new Date(outTIme));

        //TODO: check that the fare generated and out time are populated correctly in the database
        ticketDAO.GET_TICKET_OUT = DBConstants.GET_TICKET;
        Ticket resTicket = ticketDAO.getTicket(vehiculeRegNumber);
        System.out.println(resTicket.getPrice());
        assertEquals(Fare.CAR_RATE_PER_HOUR, resTicket.getPrice());
        assertTrue(resTicket.getOutTime().getTime() == outTIme);
    }

    /**
     * Test de la réduction de 5% pour les utilisateurs récurrent
     */
    @Test
    public void testRecurringDiscount() throws Exception {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(vehiculeRegNumber);
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();
        Ticket ticketOne = ticketDAO.getTicket(vehiculeRegNumber);
        long outTImeOne = (ticketOne.getInTime().getTime() + (60 * 60 * 1000));
        parkingService.processExitingVehicle(new Date(outTImeOne));

        ticketDAO.GET_TICKET_OUT = DBConstants.GET_TICKET;
        Ticket resTicketOne = ticketDAO.getTicket(vehiculeRegNumber);
        System.out.println(resTicketOne.getPrice());
        assertEquals(Fare.CAR_RATE_PER_HOUR, resTicketOne.getPrice());
        assertTrue(resTicketOne.getOutTime().getTime() == outTImeOne);
        ticketDAO.GET_TICKET_OUT = DBConstants.GET_TICKET_OUT;

        parkingService.processIncomingVehicle();
        Ticket ticket = ticketDAO.getTicket(vehiculeRegNumber);
        long outTIme = (ticket.getInTime().getTime() + (60 * 60 * 1000));
        parkingService.processExitingVehicle(new Date(outTIme));

        ticketDAO.GET_TICKET_OUT = DBConstants.GET_TICKET;
        Ticket resTicket = ticketDAO.getTicket(vehiculeRegNumber);
        System.out.println(resTicket.getPrice());
        assertEquals(Fare.CAR_RATE_PER_HOUR - (Fare.CAR_RATE_PER_HOUR * Fare.DISCOUNT_RECURRING), resTicket.getPrice());
        assertTrue(resTicket.getOutTime().getTime() == outTIme);
    }

    @Test
    public void testIncomingAlreadyParkVehicle() throws Exception {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(vehiculeRegNumber);
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();
        assertTrue(ticketDAO.isNotAlreadyIn(vehiculeRegNumber) == false);
    }
}