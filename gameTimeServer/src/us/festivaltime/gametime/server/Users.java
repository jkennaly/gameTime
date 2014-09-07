package us.festivaltime.gametime.server;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServlet;
import javax.sql.DataSource;
import java.sql.*;

/**
 * Created by jbk on 8/31/14.
 */
public class Users extends HttpServlet{
    String hashedpw, salt, username, email, level, all_keys, mobile_auth_key, follows, blocks;
    int id;
    public Users (String unameOrEmail){
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
        PreparedStatement getUserByName = null;

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {

            con = DriverManager.getConnection("jdbc:mysql://localhost/festival_master?" + "user=festival_master&password=testPASSWORD");

            con.setAutoCommit(false);

            /*
            Properties properties = new Properties();
            try {
                properties.load(getServletContext().getResourceAsStream("/WEB-INF/queries.properties"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            String qText = properties.getProperty("getUserByNameOrEmail");
            */
        //    String qText = "SELECT `id`, `hashedpw`, `salt`, `username`, `email`, `level`, `all_keys`, `mobile_auth_key`, `follows`, " +
        //            "`blocks`, count(*) AS total_rows FROM `Users` WHERE `deleted`!='1' AND (`email`=? OR `username`=?)";

            String qText = "SELECT `id`, `hashedpw`, `salt`, `username`, `email`, `level`, `all_keys`, `mobile_auth_key`, `follows`, " +
                    "`blocks`, count(*) AS total_rows FROM `Users` WHERE `deleted`!='1' AND (`email`=? OR `username`=?)";
       //     System.out.println("Query: " + qText);
            /*
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(qText);
            */

            getUserByName = con.prepareStatement(qText, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            getUserByName.setString(1, unameOrEmail);
            getUserByName.setString(2, unameOrEmail);
            ResultSet rs = getUserByName.executeQuery();

            if (!rs.isBeforeFirst() ) {
                System.out.println("No data");
            } else {
                rs.next();

                if (rs.getInt("total_rows") == 1) {

                    hashedpw = rs.getString("hashedpw");
                    salt = rs.getString("salt");
                    username = rs.getString("username");
                    email = rs.getString("email");
                    level = rs.getString("level");
                    all_keys = rs.getString("all_keys");
                    mobile_auth_key = rs.getString("mobile_auth_key");
                    follows = rs.getString("follows");
                    blocks = rs.getString("blocks");
                    id = rs.getInt("id");

                } else new InstantiationException("The username or email address was not valid");
            }

            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    public void storeResponse(long challenge) throws SQLException {
        Connection con = null;
        PreparedStatement store = null;

        String challPW = hashedpw + challenge;
        String hashed = Hashing.sha256()
                .hashString(challPW, Charsets.UTF_8)
                .toString();

//        System.out.println("Expected response: " + hashed);

        con = DriverManager.getConnection("jdbc:mysql://localhost/festival_master?" + "user=festival_master&password=testPASSWORD");

        con.setAutoCommit(false);
        String qText = "UPDATE `Users` SET `all_keys`=? WHERE `id`=?";

        store = con.prepareStatement(qText);

        store.setString(1, hashed);
        store.setInt(2, id);
        store.executeUpdate();
        con.commit();
        con.close();

    }

    public String generateAuthKey() throws SQLException {
        Connection con = null;
        PreparedStatement store = null;

        long time = System.currentTimeMillis();

        String hashed = Hashing.sha256()
                .hashLong(time)
                .toString();

        con = DriverManager.getConnection("jdbc:mysql://localhost/festival_master?" + "user=festival_master&password=testPASSWORD");

        con.setAutoCommit(false);
        String qText = "UPDATE `Users` SET `mobile_auth_key`=? WHERE `id`=?";

        store = con.prepareStatement(qText);

        store.setString(1, hashed);
        store.setInt(2, id);
        store.executeUpdate();
        con.commit();
        con.close();

        return hashed;
    }
}
