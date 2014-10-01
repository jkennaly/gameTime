package us.festivaltime.gametime.server;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

/**
 * Created by jbk on 9/13/14.
 */
public class DBConnect {


   static JSONArray dbQuery (String qText, String[] args) throws SQLException, JSONException {
        Connection con;
       PreparedStatement stmt;
        con = getDBConnection();

        con.setAutoCommit(false);
        stmt = con.prepareStatement(qText, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

        for(int i=0;i<args.length;i++) stmt.setString((i + 1), args[i]);
        ResultSet rs = stmt.executeQuery();
       JSONArray ja = new JSONArray();
       if (rs.isBeforeFirst()) {

           ResultSetMetaData rsmd = rs.getMetaData();
           int columnCount = rsmd.getColumnCount();

// The column count starts from 1

           while (rs.next()) {
                JSONObject jo = new JSONObject();

               for (int i = 1; i < columnCount + 1; i++) {
                   String jdbcType;
                   String name = rsmd.getColumnLabel(i);
                   int type = rsmd.getColumnType(i);
//                   System.err.println("Col name: " + name);
                   switch (type) {
                       case Types.BIT: jdbcType="BIT"; break;
                       case Types.TINYINT: jdbcType="TINYINT"; break;
                       case Types.SMALLINT: jdbcType="SMALLINT"; break;
                       case Types.INTEGER: jdbcType="INTEGER"; break;
                       case Types.BIGINT: jdbcType="BIGINT"; break;
                       case Types.FLOAT: jdbcType="FLOAT"; break;
                       case Types.REAL: jdbcType="REAL"; break;
                       case Types.DOUBLE: jdbcType="DOUBLE"; break;
                       case Types.NUMERIC: jdbcType="NUMERIC"; break;
                       case Types.DECIMAL: jdbcType="DECIMAL"; break;
                       case Types.CHAR: jdbcType="CHAR"; break;
                       case Types.VARCHAR: jdbcType="VARCHAR"; break;
                       case Types.LONGVARCHAR: jdbcType="LONGVARCHAR"; break;
                       case Types.DATE: jdbcType="DATE"; break;
                       case Types.TIME: jdbcType="TIME"; break;
                       case Types.TIMESTAMP: jdbcType="TIMESTAMP"; break;
                       case Types.BINARY: jdbcType="BINARY"; break;
                       case Types.VARBINARY: jdbcType="VARBINARY"; break;
                       case Types.LONGVARBINARY: jdbcType="LONGVARBINARY"; break;
                       case Types.NULL: jdbcType="NULL"; break;
                       case Types.OTHER: jdbcType="OTHER"; break;
                       case Types.JAVA_OBJECT: jdbcType="JAVA_OBJECT"; break;
                       case Types.DISTINCT: jdbcType="DISTINCT"; break;
                       case Types.STRUCT: jdbcType="STRUCT"; break;
                       case Types.ARRAY: jdbcType="ARRAY"; break;
                       case Types.BLOB: jdbcType="BLOB"; break;
                       case Types.CLOB: jdbcType="CLOB"; break;
                       case Types.REF: jdbcType="REF"; break;
                       default: jdbcType="Unknown[" + type + "]"; break;
                   }
//                   System.err.println("Col type: " + jdbcType);
                   switch(jdbcType) {
                       case "CHAR":
                       case "VARCHAR":
                       case "LONGVARCHAR":
                       case "LONGVARBINARY":
                           String result;
                           result = rs.getString(name);
                           jo.put(name, result);
                           break;
                       case "DATE":
                           Date result1;
                           result1 = rs.getDate(name);
                           jo.put(name, result1);
                           break;
                       case "TIME":
                           Time result2;
                           result2 = rs.getTime(name);
                           jo.put(name, result2);
                           break;
                       case "TINYINT":
                           int result3;
                           result3 = (int) rs.getByte(name);
                           jo.put(name, result3);
                           break;
                       case "SMALLINT":
                           int result4;
                           result4 = (int) rs.getShort(name);
                           jo.put(name, result4);
                           break;
                       case "INTEGER":
                           int result5;
                           result5 = rs.getInt(name);
                           jo.put(name, result5);
                           break;
                       case "BIGINTEGER":
                           long result8;
                           result8 = rs.getLong(name);
                           jo.put(name, result8);
                           break;
                       case "FLOAT":
                       case "DOUBLE":
                       case "REAL":
                           double result6;
                           result6 = (double) rs.getInt(name);
                           jo.put(name, result6);
                           break;
                       case "TIMESTAMP":
                           Timestamp result7;
                           result7 = rs.getTimestamp(name);
                           jo.put(name, result7);
                           break;
                       case "BLOB":
                           String result9;
                           byte[] bytes = rs.getBytes(name);
                           // result9 = new String(bytes);
                           result9 = Base64.encodeBase64URLSafeString(bytes);
                           jo.put(name, result9);
                           break;
                        default: break;
                   }


               }
               ja.put(jo);

           }
       } else {
//           System.err.println("No data");
       }

       close(rs, stmt, con);
        return ja;
    }

    private static Connection getDBConnection() throws SQLException {

        // Obtain our environment naming context
        Context initCtx;
        Context envCtx;
        try {
            initCtx = new InitialContext();
            envCtx = (Context) initCtx.lookup("java:comp/env");

// Look up our data source

            assert envCtx != null;
        } catch (NamingException e) {
            e.printStackTrace();
        }

// Allocate and use a connection from the pool
        Connection con;

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
        }


        Properties dbConnect = new Properties();
        try {
            dbConnect.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("dbconnect.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        // String connectString = "jdbc:mysql://localhost/festival_master?" + "user=festival_master&password=testPASSWORD";
        String connectString = "jdbc:mysql://" + dbConnect.getProperty("host") + "/" + dbConnect.getProperty("db") +
                "?user=" + dbConnect.getProperty("username") + "&password=" + dbConnect.getProperty("password");

        con = DriverManager.getConnection(connectString);

        return con;

    }

    public static void updateRecord(String sql, JSONArray param, JSONArray types, String[] trackers, int[] trackerParams) throws SQLException {

        Connection con;

        try {
            con = getDBConnection();
            con.setAutoCommit(false);

            PreparedStatement store = con.prepareStatement(sql);
            try {
                for (int i = 0; i < param.length(); i++) {
                    switch (types.getString(i)) {
                        case "int":
                            store.setInt((i + 1), param.getInt(i));
                            break;
                        case "String":
                            store.setString((i + 1), param.getString(i));

                            break;
                        default:
                            break;
                    }

                }
            }   catch (JSONException e) {
            e.printStackTrace();
        }


//            System.err.println(updateTableSQL);

            // execute update SQL statement
            store.executeUpdate();
            long javaTime = System.currentTimeMillis();
            for (int j = 0; j < trackers.length; j++) {
                String tracker = trackers[j];
                tracker = "tracker_" + tracker;
                String updT = "INSERT INTO " + tracker + " (item, javaTime) VALUES " +
                        "( ?, ? );";
                PreparedStatement tracking = con.prepareStatement(updT);
                tracking.setInt(1, trackerParams[j]);
                tracking.setLong(2, javaTime);
                tracking.executeUpdate();
            }
            con.commit();
            close(store, con);

 //           System.err.println("Record is updated to DBUSER table!");

        } catch (SQLException e) {

            System.err.println(e.getMessage());

        }

    }

    public static void close(ResultSet rs, Statement ps, Connection conn) {
        if (ps != null) {
            try {
                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (rs != null) {
            try {
                rs.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    public static void close(PreparedStatement ps, Connection conn) {
        if (ps != null) {
            try {
                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
    }
}
