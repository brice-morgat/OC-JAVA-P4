package com.parkit.parkingsystem;

import com.parkit.parkingsystem.config.DataBaseConfig;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DataBaseTest {
    private static DataBaseConfig dataBaseTestConfig = new DataBaseConfig();

    @Test
    public void getConnectionTest() throws SQLException, ClassNotFoundException {
        Connection db = null;
        db = dataBaseTestConfig.getConnection();
        PreparedStatement rs = db.prepareStatement("SELECT * FROM ticket");
        rs.executeQuery();
        dataBaseTestConfig.closePreparedStatement(rs);
        dataBaseTestConfig.closeConnection(db);
    }

    @Test
    public void errorConnectionTest() throws SQLException, ClassNotFoundException {
        Connection db = null;
        db = dataBaseTestConfig.getConnection();
        PreparedStatement rs = db.prepareStatement("SELECT * FROM ticket");
        rs.executeQuery();
        dataBaseTestConfig.closePreparedStatement(rs);
        dataBaseTestConfig.closeConnection(null);
    }
}
