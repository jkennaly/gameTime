package us.festivaltime.gametime.server;


import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import sun.net.www.protocol.mailto.MailToURLConnection;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.URL;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by jbk on 8/30/14.
 */
public class GameTime {

    public static String sanitizeJsonpParam(String s) {
        if (StringUtils.isEmpty(s)) return null;
        if (!StringUtils.startsWithIgnoreCase(s, "angular.callbacks")) return null;
        if (StringUtils.length(s) > 128) return null;
        return s;
    }

    @Deprecated
    public static String getMessage(String cb) {
        String jsonCallbackParam = sanitizeJsonpParam(cb);
        if (jsonCallbackParam == null || jsonCallbackParam.isEmpty()) return "Failure";
        JSONObject jo = new JSONObject();
        try {
            jo.put("greeting", "Hello, World!");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonCallbackParam + "(" + jo.toString() + ");";
    }

    public static String parseRequest(HttpServletRequest request) {
        JSONObject jo = new JSONObject();
        JSONObject incJO = new JSONObject();
        User self;
        int festID, dateID;
        Festival curFest;
        FestivalDate curDate;


        Map<String, String[]> map = request.getParameterMap();
        java.util.Set<Map.Entry<String, String[]>> set = map.entrySet();
        Iterator it = set.iterator();
        while (it.hasNext()) {
            JSONArray params = new JSONArray();
            Map.Entry<String, String[]> entry =
                    (Map.Entry<String, String[]>) it.next();
            String paramName = entry.getKey();
            String[] paramValues = entry.getValue();
            for (int i = 0; i < paramValues.length; i++) params.put(paramValues[i]);
            try {
                incJO.put(paramName, params);
                System.out.println("k: " + paramName + "v: " + params.getString(0));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        String jsonCallbackParam = null;
        try {
            String body = getBody(request);
            //       System.out.println(body);

            if (incJO.has("callback")) {
                String cb = incJO.getJSONArray("callback").getString(0);
                jsonCallbackParam = sanitizeJsonpParam(cb);
            } else jsonCallbackParam = null;

            jo.put("response", "Failed");
            String messageJSON = jsonCallbackParam + "(\")]}',\\n\" + " + jo.toString() + ");";
            if (jsonCallbackParam == null || jsonCallbackParam.isEmpty()) return messageJSON;
            String reqType = incJO.getJSONArray("reqType").getString(0);
            String uname = incJO.getJSONArray("uname").getString(0);

            switch (reqType) {
                case "login_status":
                    jo.put("loginValid", verifyAuth(incJO.getJSONArray("mobile_auth_key").getString(0), uname));
                    break;
                case "challenge_req":
                    // retrieve salt, challenge for username
                    jo = provideChallenge(uname);
                    String from = "webserv@festivaltime.us";
                    String to = "jbk@festivaltime.us";
                    String subject = "Email Test";
                    String mailBody = "Challenge requested for " + uname;
                    sendMail(from, to, subject, mailBody, null);
                    break;
                case "challenge_sub":
                    // retrieve salt, challenge for username
                    String response = incJO.getJSONArray("response").getString(0);
//                   System.err.println("Challenge response: " + response);

                    jo = evaluateResponse(uname, response);
                    if (!jo.getBoolean("pwValid")) break;

                    self = new User(uname);
                    //Get current user data
//                    System.err.println("User instantiated: " + self.id);
                    jo.put("selfData", self.getUserData());
//                    System.err.println("User data got: " + self.id);

                    //Get selfProfile

                    //Get other user data
                    jo.put("othersData", self.getOthersData());


                    //Get festival data for purchased festivals
                    jo.put("purchasedFestivals", Festival.getPurchasedFestivalData(self));

                    //Get festival data for unpurchased festivals
                    jo.put("unpurchasedFestivals", Festival.getUnpurchasedFestivalData(self));

                    break;
                case "general_data":
                    //Verify request is valid
                    if (!verifyAuth(incJO.getJSONArray("mobile_auth_key").getString(0), uname)) {
                        jo.put("loginValid", false);
                        break;
                    }
                    self = new User(uname);
                    //Get current user data
                    jo.put("selfData", self.getUserData());

                    //Get selfProfile

                    //Get other user data
                    jo.put("othersData", self.getOthersData());

                    //Get festival data for purchased festivals
                    jo.put("purchasedFestivals", Festival.getPurchasedFestivalData(self));

                    //Get festival data for unpurchased festivals
                    jo.put("unpurchasedFestivals", Festival.getUnpurchasedFestivalData(self));

                    break;
                case "select_purchased":
                    //Verify request is valid
                    if (!verifyAuth(incJO.getJSONArray("mobile_auth_key").getString(0), uname)) {
                        jo.put("loginValid", false);
                        break;
                    }
                    //Verify festival is purchased
                    festID = Integer.parseInt(incJO.getJSONArray("selectedFestival").getString(0));
                    dateID = Integer.parseInt(incJO.getJSONArray("selectedDate").getString(0));
                    self = new User(uname);
//                    System.out.println("Request: " + request.getParameter("selectedFestival"));
                    if (!self.checkUserPurchasedFestival(festID)) {
                        jo.put("loginValid", false);
                        break;
                    }
                    curFest = new Festival(festID);
                    curDate = new FestivalDate(dateID);

                    //Return data on current festival
                    jo = getAppData(self, curFest, curDate, jo);
                    break;
                case "select_unpurchased":
                    //Verify request is valid
                    if (!verifyAuth(incJO.getJSONArray("mobile_auth_key").getString(0), uname)) {
                        jo.put("loginValid", false);
                        break;
                    }
                    //Verify festival is purchased
                    festID = Integer.parseInt(incJO.getJSONArray("selectedFestival").getString(0));
                    dateID = Integer.parseInt(incJO.getJSONArray("selectedDate").getString(0));
                    self = new User(uname);
//                    System.out.println("Request: " + request.getParameter("selectedFestival"));
                    if (self.checkUserPurchasedFestival(festID)) {
                        jo.put("loginValid", false);
                        break;
                    }
                    curFest = new Festival(festID);
                    curDate = new FestivalDate(dateID);
                    if (self.credits < curFest.cost) {
                        self.purchaseCredits();
                    }
                    self.purchaseFestival(curFest);
                    //Return data on current festival
                    jo = getAppData(self, curFest, curDate, jo);
                    jo.put("response", "success");
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
        } catch (IOException e) {
            e.printStackTrace();
        }
//        System.out.println("Reply: " + jo.toString());
        return jsonCallbackParam + "(" + jo.toString() + ");";
    }

    private static JSONObject getAppData(User self, Festival curFest, FestivalDate curDate, JSONObject jo) throws JSONException {
        long preTime, postTime, timeTaken;
        preTime = System.currentTimeMillis();
        jo.put("currentFestivalData", curFest.getAppData(self, curFest));
        postTime = System.currentTimeMillis();
        timeTaken = postTime - preTime;
        preTime = postTime;
        System.out.println("FestivalData collection time: " + timeTaken);
        jo.put("dateFestivalData", curDate.getAppData(self, curFest));
        postTime = System.currentTimeMillis();
        timeTaken = postTime - preTime;
        preTime = postTime;
        System.out.println("Date Data collection time: " + timeTaken);
        //Return User festival data
        jo.put("userFestivalData", self.getAppData(self, curFest));
        postTime = System.currentTimeMillis();
        timeTaken = postTime - preTime;
        preTime = postTime;
        System.out.println("User Data collection time: " + timeTaken);
        jo.put("bandFestivalData", Band.getAppData(self, curFest));
        postTime = System.currentTimeMillis();
        timeTaken = postTime - preTime;
        preTime = postTime;
        System.out.println("Band Data collection time: " + timeTaken);
        jo.put("setFestivalData", Set.getAppData(curDate));
        postTime = System.currentTimeMillis();
        timeTaken = postTime - preTime;
        preTime = postTime;
        System.out.println("Set Data collection time: " + timeTaken);
//        System.out.println("jo: " + jo.toString());

        jo.put("dayFestivalData", Day.getAppData(curDate));
        postTime = System.currentTimeMillis();
        timeTaken = postTime - preTime;
        preTime = postTime;
        System.out.println("Day Data collection time: " + timeTaken);
        jo.put("placeFestivalData", Place.getAppData(curFest));
        postTime = System.currentTimeMillis();
        timeTaken = postTime - preTime;
        preTime = postTime;
        System.out.println("Place Data collection time: " + timeTaken);
        jo.put("messageFestivalData", Message.getAppData(self, curDate));
        postTime = System.currentTimeMillis();
        timeTaken = postTime - preTime;
        System.out.println("Message Data collection time: " + timeTaken);
        return jo;
    }

    private static boolean verifyAuth(String mak, String uname) {
        try {
            User user;
            user = new User(uname);
            if (mak.equals(user.mobile_auth_key)) return true;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

    private static JSONObject provideChallenge(String uname) throws JSONException {
        JSONObject jo = new JSONObject();
        try {
            User user = new User(uname);
            long challenge = System.currentTimeMillis();
            try {
                user.storeResponse(challenge);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            jo.put("challenge", challenge);
            jo.put("salt", user.salt);
            jo.put("uname", user.username);
        } catch (IllegalArgumentException e) {
            System.out.println("Illegal username " + uname);
        }


        return jo;
    }

    private static JSONObject evaluateResponse(String uname, String response) {
        User user = null;
        try {
            user = new User(uname);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONObject jo = new JSONObject();
//        System.err.println("uname/response/all_keys: " + uname + " / " + response + " / " + user.all_keys);

        //       System.err.println("User instantiated: " + user.id);
        assert user != null;
        if (user.all_keys.equals(response)) {
//            System.err.println("User key: " + user.all_keys);
            try {
                jo.put("auth_key", user.generateAuthKey());
                jo.put("uname", user.username);
                jo.put("pwValid", true);
            } catch (JSONException | SQLException e) {
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

    public static String getBody(HttpServletRequest request) throws IOException {

        String body = null;
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;

        try {
            InputStream inputStream = request.getInputStream();
            if (inputStream != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                char[] charBuffer = new char[128];
                int bytesRead = -1;
                while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                    stringBuilder.append(charBuffer, 0, bytesRead);
                }
            } else {
                stringBuilder.append("");
            }
        } catch (IOException ex) {
            throw ex;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ex) {
                    throw ex;
                }
            }
        }

        body = stringBuilder.toString();
        return body;
    }

    public static void sendMail(String from, String to, String subject, String body, String[] headers) throws IOException {
        System.setProperty("mail.host", "localhost");

        URL u = new URL("mailto:" + to);
        MailToURLConnection con = (MailToURLConnection) u.openConnection();
        OutputStream os = con.getOutputStream();
        OutputStreamWriter w = new OutputStreamWriter(os);

        DateFormat df = new SimpleDateFormat("E, d MMM yyyy H:mm:ss Z");
        Date d = new Date();
        String dt = df.format(d);
        String mid = d.getTime() + from.substring(from.indexOf('@'));

        w.append("Subject: " + subject + "\r\n");
        w.append("Date: " + dt + "\r\n");
        w.append("Message-ID: <" + mid + ">\r\n");
        w.append("From: " + from + "\r\n");
        w.append("To: <" + to + ">\r\n");
        if (headers != null) {
            for (String h : headers)
                w.append(h).append("\r\n");
        }
        w.append("\r\n");

        w.append(body.replace("\n", "\r\n"));
        w.flush();
        w.close();
        os.close();
        con.close();
    }

}
