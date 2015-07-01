package us.festivaltime.gametime.server;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static us.festivaltime.gametime.server.DBConnect.updateRecord;

/**
 * Created by jbk on 8/31/14.
 */
public class User extends FestivalTimeObject {
    static final String CREATE_SQL = "SELECT `id`, `hashedpw`, `salt`, `username`, `email`, `level`, `all_keys`, `mobile_auth_key`, `follows`, " +
            "`blocks`, `credits`, count(*) AS total_rows FROM `Users` WHERE `deleted`!='1' AND (`id`=?)";
    private final int id;
    String hashedpw, salt, username, email, level, all_keys, mobile_auth_key;
    int[] follows;
    int[] blocks;
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
    private User(int id, String followString, String username){

        this.id = id;
        this.username = username;
        String[] fs = followString.split("--");
        int i;
        int j = 0;
        int k;
        int[] followsTemp = new int[fs.length];
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
    }

    protected void setFields(JSONArray resultArray) throws JSONException {
        String followString, blockString;
        JSONObject rs;
        rs = resultArray.getJSONObject(0);
        if (rs.getInt("total_rows") == 0) {
            throw new IllegalArgumentException();
        }
//        System.out.println("rs: " + rs.toString());
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
        JSONArray param = new JSONArray();

        JSONArray type = new JSONArray();
        type.put("String");
        param.put(hashed);
        type.put("int");
        param.put(id);
//        System.err.println("User: " + id);
        String[] args = {};
        int[] argP = {};
        updateRecord(qText, param, type, args, argP);
    }

    public String generateAuthKey() throws SQLException {
        long time = System.currentTimeMillis();

        String hashed = Hashing.sha256()
                .hashLong(time)
                .toString();

        String qText = "UPDATE `Users` SET `mobile_auth_key`=? WHERE `id`=?";
        JSONArray param = new JSONArray();

        JSONArray type = new JSONArray();
        type.put("String");
        param.put(hashed);
        type.put("int");
        param.put(id);
//        System.err.println("User: " + id);
        String[] args = {};
        int[] argP = {};

        updateRecord(qText, param, type, args, argP);

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
        List<User> others = getVisibleUsers();
        JSONArray otherData = new JSONArray();
        for (User other : others) {
            JSONObject jo = other.getUserData();
            otherData.put(jo);
        }
        return otherData;
    }

    public JSONObject getAppData(User self, Festival fest, List<User> others) throws JSONException {
        JSONObject jo = new JSONObject();
        long time = System.currentTimeMillis();
        jo.put("appDataTime", time);

        for (User other : others) {
            //collect app data
            JSONObject userData = new JSONObject();
            userData.put("img", other.getImageString());
            userData.put("recentFests", other.getRecentFestivals());
            userData.put("purchased", other.checkUserPurchasedFestival(fest.id));
            userData.put("atFav", other.getAllTimeFavorites());
            userData.put("atWorst", other.getAllTimeWorstDisappointment());
            userData.put("gtFav", other.getGametimeFavorites(fest));
            userData.put("pgFav", other.getPregameFavorites(fest));
            userData.put("generalData", other.getUserData());
            jo.put(String.valueOf(other.id), userData);
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

    private JSONArray getPregameFavorites(Festival fest) {
        JSONArray ja = new JSONArray();
        String sql = "select band, bands.name from messages, bands where messages.band=bands.id AND fromuser=? "
                + "and remark='2' and festival=? and `mode`='1' and content>'4' order by rand() limit 5;";

        String idS = Integer.toString(id);
        String idF = Integer.toString(fest.id);
        String[] args = {idS, idF};
        try {
            JSONArray qResult = DBConnect.dbQuery(sql, args);
            for (int i = 0; i < qResult.length(); i++) {
                ja.put(qResult.getJSONObject(i).getString("name"));
            }
        } catch (SQLException | JSONException e) {
            e.printStackTrace();
        }
        return ja;
    }

    private String getAllTimeWorstDisappointment() {
        String band = "";
        String sqlP = "select band, festival, content from messages, bands "
                + "where content = (select min(content) from messages where fromuser=? and remark='2' and "
                + "`mode`='2') and messages.band=bands.id AND fromuser=? and remark='2' and `mode`='2';";

        String idS = Integer.toString(id);
        String[] args = {idS, idS};
        Integer diff = 0;
        String worstBand = "";
        String worstFestival = "";
        try {
            JSONArray qResult = DBConnect.dbQuery(sqlP, args);
            for (int i = 0; i < qResult.length(); i++) {
                String bandP = Integer.toString(qResult.getJSONObject(i).getInt("band"));
                String festival = Integer.toString(qResult.getJSONObject(i).getInt("festival"));
                Integer gameTimeRating = Integer.parseInt(qResult.getJSONObject(i).getString("content"));
                String sqlG = "select messages.band, messages.festival, messages.content, bands.name as bandName, festivals.sitename as festName from messages, bands, festivals "
                        + "where messages.band=bands.id AND messages.festival=festivals.id AND messages.fromuser=? and messages.remark='2' and messages.`mode`='1' and "
                        + "messages.`band`='" + bandP + "' and messages.`festival`='" + festival + "';";
                String[] argsG = {idS};
//                System.out.println(sqlG);
                JSONArray qResultG = DBConnect.dbQuery(sqlG, argsG);
                for (int j = 0; j < qResultG.length(); j++) {
//                    System.out.println("Result for user " + idS + ": " + qResultG.toString());
                    String bandG = qResultG.getJSONObject(j).getString("bandName");
                    String festivalG = qResultG.getJSONObject(j).getString("festName");
                    Integer pregameRating = Integer.parseInt(qResultG.getJSONObject(j).getString("content"));
                    Integer testDiff = pregameRating - gameTimeRating;
                    if (pregameRating > 0 && testDiff > diff) {
                        worstBand = bandG;
                        worstFestival = festivalG;
                        diff = testDiff;
                    }
                }
                if (diff > 0) band = worstBand + " at " + worstFestival;

            }
        } catch (SQLException | JSONException e) {
            e.printStackTrace();
        }

        return band;
    }

    private JSONArray getGametimeFavorites(Festival fest) {
        JSONArray ja = new JSONArray();
        String sql = "select band, bands.name from messages, bands where messages.band=bands.id AND fromuser=? "
                + "and remark='2' and festival=? and `mode`='2' and content>'4' order by rand() limit 5;";

        String idS = Integer.toString(id);
        String idF = Integer.toString(fest.id);
        String[] args = {idS, idF};
        try {
            JSONArray qResult = DBConnect.dbQuery(sql, args);
            for (int i = 0; i < qResult.length(); i++) {
                ja.put(qResult.getJSONObject(i).getString("name"));
            }
        } catch (SQLException | JSONException e) {
            e.printStackTrace();
        }
        return ja;
    }

    private JSONArray getAllTimeFavorites() {
        JSONArray ja = new JSONArray();
        String sql = "select band, (1+count(*)*0.01)*avg(content) as factor, bands.name from messages, "
                + "bands where messages.band=bands.id AND fromuser = ? and remark = 2 group by band "
                + "order by factor desc limit 5;";
        String idS = Integer.toString(id);
        String[] args = {idS};
        try {
            JSONArray qResult = DBConnect.dbQuery(sql, args);
            for (int i = 0; i < qResult.length(); i++) {
                ja.put(qResult.getJSONObject(i).getString("name"));
            }
        } catch (SQLException | JSONException e) {
            e.printStackTrace();
        }
        return ja;
    }

    private JSONArray getRecentFestivals() {
        JSONArray ja = new JSONArray();
        String sql = "select sitename as name from messages, festivals " +
                "where messages.festival=festivals.id and fromuser=? and messages.`mode`='2' group by festival;";
        String idS = Integer.toString(id);
        String[] args = {idS};
        try {
            JSONArray qResult = DBConnect.dbQuery(sql, args);
            for (int i = 0; i < qResult.length(); i++) {
                ja.put(qResult.getJSONObject(i).getString("name"));
            }
        } catch (SQLException | JSONException e) {
            e.printStackTrace();
        }
        return ja;
    }

    private JSONObject getImageString() {
        String sql = "SELECT `scaled_pic`, `type` FROM `pics_users` WHERE `user` = ? order by `id` DESC limit 1";
        String idS = Integer.toString(id);
        String[] args = {idS};
        String imageUrl = "https://www.festivaltime.us/includes/content/blocks/getSUserPicture.php?user=" + idS;
        idS = "userImage-" + idS;
        JSONObject jo = new JSONObject();
        try {
            JSONArray qResult = DBConnect.dbQuery(sql, args);
            if (qResult.length() > 0) {
                // String finalImage = "data:" + qResult.getJSONObject(0).getString("type") + ";base64, " + qResult.getJSONObject(0).getString("scaled_pic");
                jo.put(idS, imageUrl);
                jo.put("userImage", true);
            } else jo.put("userImage", false);
        } catch (SQLException | JSONException e) {
            e.printStackTrace();
        }
        return jo;
    }

    List<User> getVisibleUsers() {
        List<User> users = new ArrayList<>();
        long preTime, postTime, timeTaken;

        String sql = "select `id` from `Users` where `deleted`!='1' and blocks not like '%--" + id + "--%' ";
        for (int block : blocks) {
            sql = sql + "and `id`!='" + block + "' ";
        }
        String[] args = {};
        try {
            preTime = System.currentTimeMillis();
            JSONArray rawUsers = DBConnect.dbQuery(sql, args);
            postTime = System.currentTimeMillis();
            timeTaken = postTime - preTime;
            preTime = postTime;
            System.out.println("bandFestivalData: get visible users for cards time: " + timeTaken);

            for (int i = 0; i < rawUsers.length(); i++) {
                int userId = rawUsers.getJSONObject(i).getInt("id");

                User user = new User(userId);
                postTime = System.currentTimeMillis();
                timeTaken = postTime - preTime;
                preTime = postTime;
                System.out.println("bandFestivalData: construct a user for cards time: " + timeTaken);
                if (user.getSetting(68) == 1 || Arrays.asList(user.follows).contains(this.id)) users.add(user);postTime = System.currentTimeMillis();
                timeTaken = postTime - preTime;
                preTime = postTime;
                System.out.println("bandFestivalData: add a user to list for cards time: " + timeTaken);
            }
        } catch (SQLException | JSONException e) {
            e.printStackTrace();
        }
        return users;
    }

    public JSONObject getUserData() throws JSONException {
        JSONObject selfData = new JSONObject();
        selfData.put("id", id);
        selfData.put("username", username);
        JSONArray followsArray = new JSONArray();
        for (int i = 0; i < follows.length; i++) {
            followsArray.put(i, follows[i]);
        }
        selfData.put("follows", followsArray);
        /*
        JSONArray blocksArray = new JSONArray();
        for (int i = 0; i < blocks.length; i++) {
            blocksArray.put(i, blocks[i]);
        }
        selfData.put("blocks", blocksArray);
        */
        return selfData;
    }

    public void purchaseCredits() {
        if (credits < 5) {
            credits = credits + 5;

        }
    }

    public void purchaseFestival(Festival curFest) {
        //festival monitor query
        long systemTime = System.currentTimeMillis() / 1000;
        String qText = "INSERT INTO `festival_monitor` (`user`, `festival`, `phptime`, `deleted`) VALUES (?, ?, ?, '0');";
        JSONArray param = new JSONArray();
        JSONArray type = new JSONArray();
        type.put("int");
        param.put(id);
        type.put("int");
        param.put(curFest.id);
        type.put("long");
        param.put(systemTime);
//        System.err.println("User: " + id);
        String[] args = {"users"};
        int[] argP = {id};

        try {
            updateRecord(qText, param, type, args, argP);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //Debit credits
        qText = "UPDATE `Users` SET `credits`=? WHERE `id`=?;";
        param = new JSONArray();
        type = new JSONArray();
        type.put("int");
        int afterPurchase = credits - curFest.cost;
        param.put(afterPurchase);
        type.put("int");
        param.put(id);
//        System.err.println("User: " + id);
        args = new String[0];
        argP = new int[0];

        try {
            updateRecord(qText, param, type, args, argP);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    int getSetting(int settingId) {
        int settingValue = 0;
        String sql = "select `value` from `user_setting_current` where (`user`='0' OR `user`=?) AND `user_setting`=? order by `user` desc";
        String[] args = {Integer.toString(id), Integer.toString(settingId)};
        JSONArray userSettings = null;
        try {
            userSettings = DBConnect.dbQuery(sql, args);
            settingValue = userSettings.getJSONObject(0).getInt("value");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return settingValue;
    }
}
