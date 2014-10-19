package us.festivaltime.gametime.server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jbk on 10/10/14.
 */

public class Place extends FestivalTimeObject {
    static final String CREATE_SQL = "select `id`, `name`, `type`, `layout`, `priority`" +
            "from `places` " +
            "where `id`=? and deleted!='1';";
    int id, type;
    String name;

    public Place(int idS) throws JSONException {

        super(idS, CREATE_SQL);

    }

    protected static List<Place> getFestivalPlaces(Festival fest) {
        String sql = "select `id` from `places` where `festival`=? and deleted!='1'";
        String[] args = {Integer.toString(fest.id)};
        List<Place> places = new ArrayList<>();
        try {
            JSONArray rawUsers = DBConnect.dbQuery(sql, args);
            for (int i = 0; i < rawUsers.length(); i++) places.add(new Place(rawUsers.getJSONObject(i).getInt("id")));
        } catch (SQLException | JSONException e) {
            e.printStackTrace();
        }
        return places;

    }

    static JSONObject getAppData(Festival fest) throws JSONException {
        JSONObject appData = new JSONObject();
        List<Place> places = getFestivalPlaces(fest);
        for (Place place : places) {
            JSONObject tempData = new JSONObject();
            tempData.put("id", place.id);
            appData.put(Integer.toString(place.id), tempData);
        }
        return appData;
    }

    @Override
    void setFields(JSONArray resultArray) throws JSONException {

    }
}
