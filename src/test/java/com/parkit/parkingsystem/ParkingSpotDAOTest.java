package com.parkit.parkingsystem.dao;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.model.ParkingSpot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ParkingSpotDAOTest {

    @Mock
    private static DataBaseTestConfig dataBaseTestConfig;
    @Mock
    private Connection con;
    @Mock
    private PreparedStatement preparedStatement;
    @Mock
    private ResultSet resultSet;

    ParkingSpot parkingSpot;
    private static ParkingSpotDAO parkingSpotDAO;

    @BeforeEach
    private void setUp() throws Exception {

        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;

        when(dataBaseTestConfig.getConnection()).thenReturn(con);
        when(con.prepareStatement(anyString())).thenReturn(preparedStatement);
    }

    @DisplayName("Test pour vérifier si une place de parking est libre en BDD")
    @Test
    void nextParkingSpotShouldIsFound() throws SQLException {
        // WHEN
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        int result = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);

        // THEN
        assertEquals(0, result); // la méthode renvoie -1 si la connection echoue donc 0 si elle fonctionne
    }

    @DisplayName("Test pour vérifier si une place de parking est libre en BDD pour une voiture")
    @Test
    void nextParkingSpotShouldIsFoundCar() throws SQLException {
        // WHEN
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt(1)).thenReturn(2);
        int result = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);

        // THEN
        assertEquals(2, result);
    }

    @DisplayName("Test pour mettre à jour la disponibilité du parking en BDD pour une voiture")
    @Test
    void updateParkingSpot_shouldForCar() throws SQLException {
        // GIVEN
        parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        // WHEN
        when(preparedStatement.executeUpdate()).thenReturn(1, 1);
        boolean result = parkingSpotDAO.updateParking(parkingSpot);

        // THEN
        assertTrue(result);
    }

    @DisplayName("Test pour mettre à jour la disponibilite du parking en BDD pour un vélo")
    @Test
    void updateParkingSpotShouldForBike() throws SQLException {
        // GIVEN
        parkingSpot = new ParkingSpot(4, ParkingType.BIKE, false);
        // WHEN
        when(preparedStatement.executeUpdate()).thenReturn(1, 4);
        boolean result = parkingSpotDAO.updateParking(parkingSpot);

        // THEN
        assertTrue(result);
    }

    @DisplayName("Test pour une place de parking non trouvée")
    @Test
    void nextParkingSpotShouldIsNotFoundCar() throws SQLException {
        // WHEN
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        int result = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);

        // THEN
        assertEquals(-1, result);
    }

    @DisplayName("Test pour une erreur lors de la mise à jour d'une place de parking")
    @Test
    void updateShouldFail() throws SQLException {
        // GIVEN
        parkingSpot = new ParkingSpot(4, ParkingType.CAR, false);
        // WHEN
        when(preparedStatement.executeUpdate()).thenThrow(SQLException.class);
        boolean result = parkingSpotDAO.updateParking(parkingSpot);
        // THEN
        assertFalse(result);
    }
}