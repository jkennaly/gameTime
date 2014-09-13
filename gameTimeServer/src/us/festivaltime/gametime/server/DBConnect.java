package us.festivaltime.gametime.server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

/**
 * Created by jbk on 9/13/14.
 */
public class DBConnect {


   static JSONArray dbQuery (String qText, String[] args) throws SQLException, JSONException {
        // Obtain our environment naming context
        Context initCtx = null;
        Context envCtx = null;
        DataSource ds = null;
        try {
            initCtx = new InitialContext();
            envCtx = (Context) initCtx.lookup("java:comp/env");

// Look up our data source

            assert envCtx != null;
            ds = (DataSource)
                    envCtx.lookup("jdbc/festival_master");
        } catch (NamingException e) {
            e.printStackTrace();
        }

// Allocate and use a connection from the pool
        Connection con = null;
        PreparedStatement stmt = null;

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
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

        con.setAutoCommit(false);
        stmt = con.prepareStatement(qText, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

        for(int i=0;i<args.length;i++) stmt.setString((i + 1), args[i]);
        ResultSet rs = stmt.executeQuery();
       JSONArray ja = null;
       if (!rs.isBeforeFirst() ) {
           System.out.println("No data");
       } else {

           ResultSetMetaData rsmd = rs.getMetaData();
           int columnCount = rsmd.getColumnCount();

// The column count starts from 1

           while (rs.next()) {
                JSONObject jo = null;

               for (int i = 1; i < columnCount + 1; i++) {
                   String name = rsmd.getColumnLabel(i);
                   jo.put(name, rs.getString(name));
               }

           }
       }

       close(rs, stmt, con);
        return ja;
    }

    public static void close(ResultSet rs, Statement ps, Connection conn)
    {
        if (rs!=null)
        {
            try
            {
                rs.close();

            }
            catch(SQLException e)
            {
                e.printStackTrace();
            }
        }
        if (ps != null)
        {
            try
            {
                ps.close();
            } catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
        if (conn != null)
        {
            try
            {
                conn.close();
            } catch (SQLException e)
            {
                e.printStackTrace();
            }
        }

    }
}
