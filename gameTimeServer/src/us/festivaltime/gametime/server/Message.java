package us.festivaltime.gametime.server;

import org.intellij.lang.annotations.Language;
import org.json.JSONArray;
import org.json.JSONException;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by jbk on 10/10/14.
 */

public class Message extends FestivalTimeObject {

    static boolean userRatedSet(User user, Set set) {
        boolean retVal = false;
        @Language("MySQL") String sql = "select `id` from `messages` " +
                "where `fromuser`=? and `remark`='2' and `deleted`!='1'" +
                " and `set`=? ";
        String[] args = {
                Integer.toString(user.id),
                Integer.toString(set.id)
        };
        JSONArray msgRes;
        try {
            msgRes = DBConnect.dbQuery(sql, args);
            if (msgRes.length() > 0) retVal = true;
        } catch (SQLException | JSONException e) {
            e.printStackTrace();
        }
        return retVal;
    }

    static int userGtRating(User user, Set set) {
        int retVal = 0;
        @Language("MySQL") String sql = "select `content` from `messages` " +
                "where `fromuser`=? and `remark`='2' and `deleted`!='1'" +
                " and  `set`=? order by `id` DESC LIMIT 1";
        String[] args = {
                Integer.toString(user.id),
                Integer.toString(set.id)
        };
        JSONArray msgRes;
        try {
            msgRes = DBConnect.dbQuery(sql, args);
            if (msgRes.length() > 0) retVal = msgRes.getJSONObject(0).getInt("content");
        } catch (SQLException | JSONException e) {
            e.printStackTrace();
        }
        return retVal;
    }

    static int userPgRating(User user, Band band, Festival fest) {
        int retVal = 0;
        @Language("MySQL") String sql = "select `content` from `messages` " +
                "where `fromuser`=? and `remark`='2' and `deleted`!='1'" +
                " and  `band`=? and  `festival`=? and `mode`='1' order by `id` DESC LIMIT 1";
        String[] args = {
                Integer.toString(user.id),
                Integer.toString(band.id),
                Integer.toString(fest.id)
        };
        JSONArray msgRes;
        try {
            msgRes = DBConnect.dbQuery(sql, args);
            if (msgRes.length() > 0) retVal = msgRes.getJSONObject(0).getInt("content");
        } catch (SQLException | JSONException e) {
            e.printStackTrace();
        }
        return retVal;
    }

    static String userGtComment(User user, Set set) {
        String retVal = "";
        @Language("MySQL") String sql = "select `content` from `messages` " +
                "where `fromuser`=? and `remark`='1' and `deleted`!='1'" +
                " and  `set`=? order by `id` ASC ";
        String[] args = {
                Integer.toString(user.id),
                Integer.toString(set.id)
        };
        JSONArray msgRes;
        try {
            msgRes = DBConnect.dbQuery(sql, args);
            if (msgRes.length() > 0) retVal += msgRes.getJSONObject(0).getString("content");
        } catch (SQLException | JSONException e) {
            e.printStackTrace();
        }
        return retVal;
    }

    static String userPgComment(User user, Band band, Festival fest) {
        String retVal = "";
        @Language("MySQL") String sql = "select `content` from `messages` " +
                "where `fromuser`=? and `remark`='1' and `deleted`!='1'" +
                " and  `band`=? and `festival`=? order by `id` ASC ";
        String[] args = {
                Integer.toString(user.id),
                Integer.toString(band.id),
                Integer.toString(fest.id)
        };
        JSONArray msgRes;
        try {
            msgRes = DBConnect.dbQuery(sql, args);
            if (msgRes.length() > 0) retVal += msgRes.getJSONObject(0).getString("content");
        } catch (SQLException | JSONException e) {
            e.printStackTrace();
        }
        return retVal;
    }

    static JSONArray getAppData(User self, FestivalDate curDate, List<User> others) throws JSONException {
        JSONArray msgRes = new JSONArray();
        String sql = "SELECT *  FROM `messages` " +
                "WHERE" +
                //Get pregame messages about the fest, and gametime messages about the date
                " ((`festival`=? and `mode`='1') or ( `date`=? and `mode`='2' ))" +
                //Do not get deleted messages
                " and `deleted`!='1'" +
                //Only get public messages or private msgs to this user
                " and (`privacy`='1' or (`privacy`='2' and `touser`=?))" +
                //Only get messages from visible users
                " and ( `fromuser`='" + self.id + "' ";
        for (User user : others) sql = sql + " or `fromuser`='" + user.id + "' ";
        sql = sql + ")";
        String[] args = {Integer.toString(
                curDate.getFestivalId()),
                Integer.toString(curDate.id),
                Integer.toString(self.id)
        };
//        System.out.println(sql);
        try {
            msgRes = DBConnect.dbQuery(sql, args);
        } catch (SQLException | JSONException e) {
            e.printStackTrace();
        }
        return msgRes;
    }

    @Override
    void setFields(JSONArray resultArray) throws JSONException {


    }
}
