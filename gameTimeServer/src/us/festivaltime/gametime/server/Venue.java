package us.festivaltime.gametime.server;

import org.joda.time.DateTimeZone;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jbk on 9/27/14.
 */
public class Venue extends FestivalTimeObject {
    static final String CREATE_SQL = "select `id`, `name`, `description`, " +
            "timezone, country, state, city, street_address " +
            "from venues " +
            "where `id`=? and deleted!='1';";
    int id;
    String name, description, country, state, city, address;
    DateTimeZone timeZone;

    Venue(int idS) throws JSONException {
        super(idS, CREATE_SQL);
    }

    protected void setFields(JSONArray resultArray) throws JSONException {
        JSONObject rs;
        rs = resultArray.getJSONObject(0);
        name = rs.getString("name");
        description = rs.getString("description");
        id = rs.getInt("id");
        country = rs.getString("country");
        state = rs.getString("state");
        city = rs.getString("city");
        address = rs.getString("street_address");
//       System.err.println("Fest instantiation: sabout to get timezone " + rs.getString("timezone"));
        timeZone = DateTimeZone.forID(rs.getString("timezone"));
//        System.err.println("Fest instantiation: timezone done");


    }

    @Override
    JSONObject getAppData(User self, Festival fest) throws JSONException {
        JSONObject appData = new JSONObject();
        appData.put("id", id);
        appData.put("name", name);
        appData.put("description", description);
        appData.put("country", country);
        appData.put("state", state);
        appData.put("city", city);
        appData.put("address", address);
        appData.put("timeZone", timeZone.getID());

        return appData;
    }

}
