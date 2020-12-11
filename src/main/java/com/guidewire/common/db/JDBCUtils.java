package com.guidewire.common.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JDBCUtils {
    private static final Logger log = LoggerFactory.getLogger(JDBCUtils.class);

    public void close(ResultSet rs, Statement stmt) {
        close(rs);
        close(stmt);
    }
    public static void close(Statement closeMe) {
        if(closeMe == null) return;
        try {
            closeMe.close();
        } catch(SQLException e) {
            log.error("Issue closing statement.",e);
        }
    }
    public static void close(ResultSet closeMe) {
        if(closeMe == null) return;
        try {
            closeMe.close();
        } catch(SQLException e) {
            log.error("Issue closing result set.",e);
        }
    }
    public static void close(Connection closeMe) {
        if(closeMe == null) return;
        try {
            closeMe.close();
        } catch(SQLException e) {
            log.error("Issue closing connection.",e);
        }
    }
}
