package us.festivaltime.gametime.server;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServlet;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

/**
 * Created by jbk on 8/31/14.
 */
public class User extends HttpServlet {
    String hashedpw, salt, username, email, level, all_keys, mobile_auth_key;
    int[] follows, blocks;
    int id;

    public User(String unameOrEmail) throws JSONException {

        String qText = "SELECT `id`, `hashedpw`, `salt`, `username`, `email`, `level`, `all_keys`, `mobile_auth_key`, `follows`, " +
                "`blocks`, count(*) AS total_rows FROM `Users` WHERE `deleted`!='1' AND (`email`=? OR `username`=?)";

        String[] args = {unameOrEmail, unameOrEmail};
        JSONArray resultArray = null;
        try {
            resultArray = DBConnect.dbQuery(qText, args);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (resultArray.length() != 1) {
            try {
                throw new InstantiationException("The username or email address was not valid");
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        } else {
            String followString, blockString;
            JSONObject rs;
            rs = resultArray.getJSONObject(0);
            hashedpw = rs.getString("hashedpw");
            salt = rs.getString("salt");
            username = rs.getString("username");
            email = rs.getString("email");
            level = rs.getString("level");
            all_keys = rs.getString("all_keys");
            mobile_auth_key = rs.getString("mobile_auth_key");
            followString = rs.getString("follows");
            blockString = rs.getString("blocks");
            String idS = rs.getString("id");
            id = Integer.parseInt(idS);

            String[] fs = followString.split("--");
            String[] bs = blockString.split("--");

            for (int i = 0; i < fs.length; i++) {
                follows[i] = Integer.parseInt(fs[i]);
            }

            for (int i = 0; i < bs.length; i++) {
                blocks[i] = Integer.parseInt(bs[i]);
            }
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
