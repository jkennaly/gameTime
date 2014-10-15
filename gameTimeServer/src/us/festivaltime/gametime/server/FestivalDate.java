package us.festivaltime.gametime.server;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by jbk on 10/11/14.
 */

public class FestivalDate extends FestivalTimeObject {
    static final String CREATE_SQL = "select `id`, `name`, `festival`, " +
            "`venue`, `basedate`, `mode` " +
            "from dates " +
            "where `id`=? and deleted!='1';";
    int id;
    String name;
    DateTime baseDate;
    Venue venue;
    List<Day> days;

    FestivalDate(int idS) throws JSONException {
        super(idS, CREATE_SQL);
    }

    protected void setFields(JSONArray resultArray) throws JSONException {
        JSONObject rs;
        rs = resultArray.getJSONObject(0);
        name = rs.getString("name");
        id = rs.getInt("id");
//            System.err.println("Fest instantiation: about to get venue ");
        venue = new Venue(rs.getInt("venue"));
        Date tempDate = (Date) rs.get("basedate");
        //     System.out.println("temp Date: " + tempDate.toString());
        LocalDate localDate = LocalDate.fromDateFields(tempDate);
        //      System.out.println("local Date: " + localDate.toString());
        baseDate = localDate.toDateTimeAtStartOfDay(venue.timeZone);
//        System.out.println("base Date: " + baseDate.toString());
        days = Day.getDateDays(this);

//            System.err.println("Fest instantiation: field set for date: "+Integer.toString(id));

    }

    JSONObject getAppData(User self, Festival fest) throws JSONException {
        JSONObject appData = new JSONObject();
        appData.put("id", id);
        appData.put("name", name);
//            System.err.println("Collecting data: venue not yet set for date: " + Integer.toString(id));
        appData.put("venueData", venue.getAppData(self, fest));
        JSONArray dayData = new JSONArray();
//            System.err.println("Collecting data: field set for date: " + Integer.toString(id));
        for (Day day : days) dayData.put(day.getAppData(self, fest));
        appData.put("dayData", dayData);
        return appData;
    }

    public int getFestivalId() {
        int festId = 0;
        String sql = "select `festival` from `dates` where `id`=? and deleted!='1'";
        String[] args = {Integer.toString(id)};
        try {
            JSONArray rawUsers = DBConnect.dbQuery(sql, args);
            festId = rawUsers.getJSONObject(0).getInt("festival");
        } catch (SQLException | JSONException e) {
            e.printStackTrace();
        }

        return festId;
    }
}