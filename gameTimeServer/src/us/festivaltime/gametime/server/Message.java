package us.festivaltime.gametime.server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jbk on 10/10/14.
 */

public class Message extends FestivalTimeObject {

    static boolean userRatedSet(User user, Set set) {
        return false;
    }

    static int userGtRating(User user, Set set) {
        return -1;
    }

    static int userPgRating(User user, Band set, Festival fest) {
        return -1;
    }

    static String userGtComment(User user, Set set) {
        return "";
    }

    static String userPgComment(User user, Band set, Festival fest) {
        return "";
    }

    @Override
    void setFields(JSONArray resultArray) throws JSONException {


    }

    JSONObject getAppData(User self, Festival fest) throws JSONException {
        return null;
    }
}
