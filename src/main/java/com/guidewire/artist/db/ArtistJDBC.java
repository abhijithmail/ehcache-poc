package com.guidewire.artist.db;

import com.guidewire.common.db.JDBCUtils;
import com.guidewire.domain.artist.Artist;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

/**
 * Overly simple. No pooling.
 */
public class ArtistJDBC {
    private static final Logger log = LoggerFactory.getLogger(ArtistJDBC.class);
    private String cacheName = "artistCache";

    public static final String JDBC_DRIVER = "org.h2.Driver";
    public static final String JDBC_URL = "jdbc:h2:file:c:/tmp/ehache/poc";

    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_NAME = "NAME";
    public static final String COLUMN_YEAR_ESTABLISHED = "YEAR_ESTABLISHED";

    public static final String CREATE_STMT = "INSERT INTO ARTIST(ID,ARTIST_NAME,YEAR_ESTABLISHED) VALUES (?,?,?)";
    public static final String READ_BY_ID_STMT = "SELECT ID, ARTIST_NAME, YEAR_ESTABLISHED FROM ARTIST WHERE ID = ?";
    public static final String UPDATE_STMT = "UPDATE ARTIST SET ARTIST_NAME=?,YEAR_ESTABLISHED=? WHERE ID = ?";
    public static final String DELETE_STMT = "DELETE FROM ARTIST WHERE ID=?";
    public static final String COUNT_STMT = "SELECT COUNT(*) FROM ARTIST WHERE ID = ?";

    private PreparedStatement createStmt;
    private PreparedStatement readStmt;
    private PreparedStatement updateStmt;
    private PreparedStatement deleteStmt;
    private PreparedStatement countStmt;

    Connection conn;

    /**
     *
     */
    public ArtistJDBC() {
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(JDBC_URL);
            createStmt = conn.prepareStatement(CREATE_STMT);
            readStmt = conn.prepareStatement(READ_BY_ID_STMT);
            updateStmt = conn.prepareStatement(UPDATE_STMT);
            deleteStmt = conn.prepareStatement(DELETE_STMT);
            countStmt = conn.prepareStatement(COUNT_STMT);
        } catch (ClassNotFoundException e) {
            log.error("missing driver?",e);
        } catch(SQLException e) {
            log.error("sql issues",e);
        } catch (Exception e) {
            log.error("issues",e);
        }
    }

    /**
     *
     * @param pArtist
     * @return
     * @throws SQLException
     */
    public Artist create(Artist pArtist) throws SQLException {
        Artist rVal = pArtist;
        try {
            createStmt.setString(1, pArtist.getId());
            createStmt.setString(2, pArtist.getName());
            createStmt.setString(3, pArtist.getYearEstablished());
            createStmt.execute();
            pArtist.setPersistent(true);
        } catch (Exception e) {
            log.error("",e);
            throw e;
        } finally {
            JDBCUtils.close(createStmt);
        }
        return rVal;
    }

    /**
     *
     * @param pID
     * @return
     * @throws SQLException
     */
    public Artist read(String pID) throws SQLException {
        Artist rVal = null;
        readStmt.setString(1,pID);
        try (ResultSet rs = readStmt.executeQuery()) {
            rs.next();
            rVal.setId(rs.getString(COLUMN_ID));
            rVal.setName(rs.getString(COLUMN_NAME));
            rVal.setYearEstablished(rs.getString(COLUMN_YEAR_ESTABLISHED));
            rVal.setPersistent(true);
        } finally {
            JDBCUtils.close(readStmt);
        }
        return rVal;
    }

    /**
     *
     * @param pArtist
     * @return
     * @throws SQLException
     */
    public Artist update(Artist pArtist) throws SQLException {
        Artist rVal = null;
        updateStmt.setString(1,pArtist.getName());
        updateStmt.setString(2,pArtist.getYearEstablished());
        updateStmt.setString(3,pArtist.getId());
        pArtist.setPersistent(true);
        int recordCount = updateStmt.executeUpdate();
        if(recordCount < 1) {
            log.error("You tried to update an Artist that does not exist.");
        } else if(recordCount > 1) {
            log.error("Congratulations! I hope you made backups. You just updated {}, Artist records",recordCount);
        }
        JDBCUtils.close(updateStmt);
        return pArtist;
    }

    /**
     *
     * @param pArtist
     * @throws SQLException
     */
    public void delete(Artist pArtist) throws SQLException {
        delete(pArtist.getId());
        pArtist.setPersistent(false);
        pArtist.setId(null);
    }

    public void delete(String key) throws SQLException {
        deleteStmt.setString(1,key);
        int recordCount = deleteStmt.executeUpdate();
       if(recordCount < 1) {
            log.error("You tried to delete an Artist that does not exist.");
        } else if(recordCount > 1) {
            log.error("Congratulations! I hope you made backups. You just deleted {}, Artist records",recordCount);
        }
        JDBCUtils.close(deleteStmt);
    }

    public Integer count(String key) throws SQLException {
        countStmt.setString(1,key);
        ResultSet rs = countStmt.executeQuery();
        rs.next();
        Integer count = rs.getInt(1);
        return count;
    }

    public boolean exists(String key) throws SQLException {
        Integer count = count(key);
        return count >= 1;
    }
    /**
     *
     */
    public void releaseResources() {
        JDBCUtils.close(createStmt);
        JDBCUtils.close(readStmt);
        JDBCUtils.close(deleteStmt);
        JDBCUtils.close(updateStmt);
        JDBCUtils.close(conn);
    }

}
