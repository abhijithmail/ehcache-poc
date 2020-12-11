package com.guidewire.artist.db;

import com.guidewire.domain.artist.Artist;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.*;

public class ArtistJDBCTest {
    private static String pid;
    private static ArtistJDBC jdbc;

    @BeforeClass
    public static void beforeClass() throws SQLException {
        pid = Long.toString(System.currentTimeMillis());
        Artist art = new Artist(pid,"Bob Dylan", "1959");
        jdbc = new ArtistJDBC();
        jdbc.create(art);
    }

    @Test
    public void read() throws SQLException {
        Artist art = jdbc.read(pid);
        assertNotNull(art);
        assertEquals(pid, art.getId());
    }

    @Test
    public void update() {
    }

    @Test
    public void delete() {
    }
}