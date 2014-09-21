package us.festivaltime.gametime.server;



import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import us.festivaltime.gametime.server.*;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;

/**
 * Created by jbk on 8/30/14.
 */
public class GameTime {

    public static String sanitizeJsonpParam(String s) {
        if ( StringUtils.isEmpty(s)) return null;
        if ( !StringUtils.startsWithIgnoreCase(s,"jquery")) return null;
        if ( StringUtils.length(s) > 128 ) return null;
        return s;
    }

    @Deprecated
    public static String getMessage(String cb) {
        String jsonCallbackParam = sanitizeJsonpParam( cb );
        if(jsonCallbackParam == null || jsonCallbackParam.isEmpty()) return "Failure";
        JSONObject jo = new JSONObject();
        try {
            jo.put("greeting", "Hello, World!");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String messageJSON = jsonCallbackParam + "(" + jo.toString() + ");";
        return messageJSON;
    }

    public static String parseRequest( HttpServletRequest request) {
        String cb = request.getParameter("callback");
        String reqType = request.getParameter("reqType");
        String login_auth = request.getParameter("mobile_auth_key");
        String uname = request.getParameter("uname");
        System.out.println("Request jo cb: " + cb);
        System.out.println("Request jo reqType: " + reqType);
        System.out.println("Request jo auth_key: " + login_auth);
        System.out.println("Request jo uname: " + uname);
        String jsonCallbackParam = sanitizeJsonpParam( cb );
        if(jsonCallbackParam == null || jsonCallbackParam.isEmpty()) return "Failure";
        JSONObject jo = new JSONObject();
        try {
            switch ( reqType ) {
                case "login_status":
                    jo.put("loginValid", verifyAuth(login_auth, uname));
                    break;
                case "challenge_req":
                    // retrieve salt, challenge for username
                    jo = provideChallenge(uname);
                    String[] args = {};
                    break;
                case "challenge_sub":
                    // retrieve salt, challenge for username
                    String response = request.getParameter("response");
                    System.out.println("Challenge response: " + response);

                    jo = evaluateResponse(uname, response);
                    break;
                case "general_data":
                    //Get current user data
                    //Get other user data
                    //Get festival data for purchased festivals
                    //Get festival data for unpurchased festivals
                    //Get account status information
                    break;
                case "festival_select_purchased":
                    //Load festival data
                    break;
                case "festival_select_unpurchased":
                    //Verify that credits are available
                    //Debit credits and change festival status to purchased
                    //Load festival data
                    break;
                case "purchase_credits":
                    //If number of credits is less than 5, add 5 credits
                    break;
                case "message_update":
                    //If jo includes new messages to store, store them
                    //Check to see if any new messages have been stored since the last update
                    //Check for new user data since last update
                    //If new messages, package new messages for delivery
                    //If new user data, update all user data
                    //Package user update time and message number
                    break;
                default:
                    return "Failure";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println("Reply: " + jo.toString());
        String messageJSON = jsonCallbackParam + "(" + jo.toString() + ");";
        return messageJSON;
    }

    private static boolean verifyAuth(String mak, String uname){
        try {
            User user;
            user = new User(uname);
            if(mak.equals(user.mobile_auth_key)) return true;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

    private static JSONObject provideChallenge(String uname) throws JSONException {
        User user = new User(uname);
        JSONObject jo = new JSONObject();
        long challenge = System.currentTimeMillis();
        try {
            user.storeResponse(challenge);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        jo.put("challenge", challenge);
        jo.put("salt", user.salt);
        jo.put("uname", user.username);
        return jo;
    }

    private static JSONObject evaluateResponse(String uname, String response){
        User user = null;
        try {
            user = new User(uname);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONObject jo = new JSONObject();

        if(user.all_keys.equals(response)){
            System.out.println("User key: " + user.all_keys);
            try {
                jo.put("auth_key", user.generateAuthKey());
                jo.put("uname", user.username);
                jo.put("pwValid", true);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            try {
                jo.put("pwValid", false);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jo;
    }

}
