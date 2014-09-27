package us.festivaltime.gametime.server;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;

import static us.festivaltime.gametime.server.DBConnect.updateRecord;

/**
 * Created by jbk on 8/31/14.
 */
public class User extends FestivalTimeObject {
    static final String CREATE_SQL = "SELECT `id`, `hashedpw`, `salt`, `username`, `email`, `level`, `all_keys`, `mobile_auth_key`, `follows`, " +
            "`blocks`, `credits`, count(*) AS total_rows FROM `Users` WHERE `deleted`!='1' AND (`id`=?)";
    String hashedpw, salt, username, email, level, all_keys, mobile_auth_key;
    int[] follows, blocks;
    int credits;

    public User(String unameOrEmail) throws JSONException {
        super();

        String qText = "SELECT `id`, `hashedpw`, `salt`, `username`, `email`, `level`, `all_keys`, `mobile_auth_key`, `follows`, " +
                "`blocks`, `credits`, count(*) AS total_rows FROM `Users` WHERE `deleted`!='1' AND (`email`=? OR `username`=?)";

        String[] args = {unameOrEmail, unameOrEmail};
        JSONArray resultArray = null;

        try {
            resultArray = DBConnect.dbQuery(qText, args);
        } catch (SQLException e) {
            e.printStackTrace();
        }

/*
        if (resultArray.length() != 1) {
            try {
                throw new InstantiationException("The username or email address was not valid");
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        } else {
        */
        setFields(resultArray);

    }

    public User(int idS) throws JSONException {

        super(idS, CREATE_SQL);

    }

    protected void setFields(JSONArray resultArray) throws JSONException {
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
        id = rs.getInt("id");
        credits = rs.getInt("credits");

        String[] fs = followString.split("--");
        String[] bs = blockString.split("--");
        int i;
        int j = 0;
        int k;
        int[] followsTemp = new int[fs.length];
        int[] blocksTemp = new int[bs.length];
        for (i = 0; i < fs.length; i++)
            try {
                followsTemp[j] = Integer.parseInt(fs[i]);
                if (followsTemp[j] > 0) j++;
            } catch (NumberFormatException ignored) {
            }
        follows = new int[j];
        for (k = 0; k < j; k++) {
            follows[k] = followsTemp[k];
        }
        j = 0;
        for (i = 0; i < bs.length; i++)
            try {
                blocksTemp[j] = Integer.parseInt(bs[i]);
                if (followsTemp[j] > 0) j++;
            } catch (NumberFormatException ignored) {

            }
        blocks = new int[j];
        for (k = 0; k < j; k++) {
            blocks[k] = blocksTemp[k];
        }
        //      }
    }


    public void storeResponse(long challenge) throws SQLException {
//        Connection con = null;
//        PreparedStatement store = null;

        String challPW = hashedpw + challenge;
        String hashed = Hashing.sha256()
                .hashString(challPW, Charsets.UTF_8)
                .toString();

//        System.err.println("Expected response: " + hashed);
        /*
        con = DriverManager.getConnection("jdbc:mysql://localhost/festival_master?" + "user=festival_master&password=testPASSWORD");

        con.setAutoCommit(false);
        */
        String qText = "UPDATE `Users` SET `all_keys`=? WHERE `id`=?";
        JSONArray param =new JSONArray();

        JSONArray type =new JSONArray();
        type.put("String");
        param.put(hashed);
        type.put("int");
        param.put(id);
//        System.err.println("User: " + id);
        updateRecord(qText, param, type);
    }

    public String generateAuthKey() throws SQLException {
        long time = System.currentTimeMillis();

        String hashed = Hashing.sha256()
                .hashLong(time)
                .toString();

        String qText = "UPDATE `Users` SET `mobile_auth_key`=? WHERE `id`=?";
        JSONArray param =new JSONArray();

        JSONArray type =new JSONArray();
        type.put("String");
        param.put(hashed);
        type.put("int");
        param.put(id);
        System.err.println("User: " + id);
        updateRecord(qText, param, type);

        return hashed;
    }

    private JSONArray getAllUsers() {
        JSONArray tempUsers = new JSONArray();
        String sql = "select `id` from `Users` where `deleted`!='1'";
        String[] args = {};
        try {
            JSONArray rawUsers = DBConnect.dbQuery(sql, args);
            for (int i = 0; i < rawUsers.length(); i++) {
                tempUsers.put(i, rawUsers.getJSONObject(i).getInt("id"));

            }
        } catch (SQLException | JSONException e) {
            e.printStackTrace();
        }
        return tempUsers;
    }

    public JSONArray getOthersData() throws JSONException {
        JSONArray others = getVisibleUsers();
        JSONArray otherData = new JSONArray();
        for (int i = 0; i < others.length(); i++) {
            User other = new User(others.getInt(i));
            JSONObject jo = other.getUserData();
            otherData.put(jo);
        }
        return otherData;
    }

    public JSONObject getAppData(User self, Festival fest) throws JSONException {
        JSONArray others = self.getVisibleUsers();
        JSONObject jo = new JSONObject();
        for (int i = 0; i < others.length(); i++) {
            JSONObject userData = new JSONObject();
            userData.put("purchased", self.checkUserPurchasedFestival(fest.id));
            jo.put(String.valueOf(self.id), userData);
        }
        return jo;
    }


    public boolean checkUserPurchasedFestival(int festID) {

        try {
            JSONArray purFests = Festival.getPurchasedFestivals(this);
            for (int i = 0; i < purFests.length(); i++) if (purFests.getInt(i) == festID) return true;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    private JSONArray getVisibleUsers() {
        JSONArray tempUsers = new JSONArray();
        String sql = "select `id` from `Users` where `deleted`!='1' and blocks not like '%--" + id + "--%' ";
        for (int block : blocks) {
            sql = sql + "and `id`!='" + block + "' ";
        }
        String[] args = {};
        try {
            JSONArray rawUsers = DBConnect.dbQuery(sql, args);
            for (int i = 0; i < rawUsers.length(); i++) {
                tempUsers.put(i, rawUsers.getJSONObject(i).getInt("id"));

            }
        } catch (SQLException | JSONException e) {
            e.printStackTrace();
        }
        return tempUsers;
    }

    public JSONObject getUserData() throws JSONException {
        JSONObject selfData = new JSONObject();
        selfData.put("id", id);
        selfData.put("username", username);
        JSONArray followsArray = new JSONArray();
        for (int i = 0; i < follows.length; i++) {
            followsArray.put(i, follows[i]);
        }
        JSONArray blocksArray = new JSONArray();
        for (int i = 0; i < blocks.length; i++) {
            blocksArray.put(i, blocks[i]);
        }
        selfData.put("follows", followsArray);
        selfData.put("blocks", blocksArray);
        return selfData;
    }
}
