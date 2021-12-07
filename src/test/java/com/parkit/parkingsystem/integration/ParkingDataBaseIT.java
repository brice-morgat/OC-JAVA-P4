package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {
    private static String vehiculeRegNumber = "ABCDEF";

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
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(vehiculeRegNumber);
        dataBasePrepareService.clearDataBaseEntries();
    }

    @AfterAll
    private static void tearDown(){
        dataBasePrepareService.clearDataBaseEntries();
    }

    @Test
    public void testParkingACar() {
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

    @Test
    public void testParkingLotExit() {
        testParkingLotExit(false);
    }

    @Test
    public void testParkingLotExit(boolean isRecurring){
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        boolean priceUpdated = false;
        boolean outTimeGenerated = false;
        testParkingACar();
        ticketDAO.GET_TICKET = DBConstants.GET_TICKET_OUT;
        Ticket ticket = ticketDAO.getTicket(vehiculeRegNumber);
        long outTIme = (ticket.getInTime().getTime() + ( 24 * 60 * 60 * 1000));
        parkingService.processExitingVehicle(new Date(outTIme));

        System.out.println("Temps à l'entrée du parking : "
                + ticket.getInTime());
        //TODO: check that the fare generated and out time are populated correctly in the database
        Ticket resTicket = ticketDAO.getTicket(vehiculeRegNumber);
        assertEquals(resTicket.getPrice(), Fare.CAR_RATE_PER_HOUR * );
        assertTrue(resTicket.getOutTime().getTime() == outTIme);

    }
}