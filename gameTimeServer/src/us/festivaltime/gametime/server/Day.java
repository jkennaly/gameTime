package us.festivaltime.gametime.server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jbk on 10/11/14.
 */

public class Day extends FestivalTimeObject {
    static final String CREATE_SQL = "select `id`, `name`, " +
            "`days_offset` " +
            "from days " +
            "where `id`=? and deleted!='1';";
    int id, offset;
    String name, description;

    Day(int idS) throws JSONException {
        super(idS, CREATE_SQL);
    }

    protected static List<Day> getDateDays(FestivalDate festivalDate) {
        int festId = festivalDate.getFestivalId();
        String sql = "select `id` from `days` where `festival`=? and deleted!='1'";
        String[] args = {Integer.toString(festId)};
        List<Day> days = new ArrayList<>();
        try {
            JSONArray rawUsers = DBConnect.dbQuery(sql, args);
            for (int i = 0; i < rawUsers.length(); i++) days.add(new Day(rawUsers.getJSONObject(i).getInt("id")));
        } catch (SQLException | JSONException e) {
            e.printStackTrace();
        }
        return days;

    }

    static JSONObject getAppData(FestivalDate date) throws JSONException {
        JSONObject appData = new JSONObject();
        List<Day> days = getDateDays(date);
        for (Day day : days) {
            JSONObject tempData = new JSONObject();
            tempData.put("id", day.id);
            tempData.put("offset", day.offset);
            tempData.put("name", day.name);
            tempData.put("description", day.description);
            appData.put(Integer.toString(day.id), tempData);
        }
        return appData;
    }

    protected void setFields(JSONArray resultArray) throws JSONException {
        JSONObject rs;
        rs = resultArray.getJSONObject(0);
        name = rs.getString("name");
        id = rs.getInt("id");
        offset = rs.getInt("days_offset");
//            System.err.println("Fest instantiation: setting fields for day: " + Integer.toString(id));


    }
}