package us.festivaltime.gametime.server;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jbk on 9/12/14.
 */
public class Festival extends FestivalTimeObject {
    static final String CREATE_SQL = "select `id`, `name`, `mode`, `dbname`, `series`, `year`, `sitename`, `start_time`, `length`, `website`, `description`, `cost`, `num_days`, `num_dates` " +
            "from festivals " +
            "where `id`=? and deleted!='1';";
    int mode, start_time, length, cost, num_days, num_dates;
    String name, dbname, sitename, website, description, year;
    FestivalSeries series;
    List<FestivalDate> dates;

    public Festival(int idS) throws JSONException {
        super(idS, CREATE_SQL);


    }

    public static JSONArray getPurchasedFestivalData(User user) throws JSONException {
        JSONArray purchased = new JSONArray();
        JSONArray festIDs = getPurchasedFestivals(user);
        for (int i = 0; i < festIDs.length(); i++) {
            System.out.println("Fest instantiation begins");
            Festival fest = new Festival(festIDs.getInt(i));
            System.err.println("Fest instantiated: " + fest.id);
            purchased.put(fest.getAppData(user, fest));
        }
        return purchased;
    }

    public static JSONArray getUnpurchasedFestivalData(User user) throws JSONException {
        JSONArray purchased = new JSONArray();
        JSONArray festIDs = getUnpurchasedFestivals(user);
        for (int i = 0; i < festIDs.length(); i++) {
            Festival fest = new Festival(festIDs.getInt(i));
            purchased.put(fest.getAppData(user, fest));
        }
        return purchased;
    }

    public static JSONArray getPurchasedFestivals(User user) {
        String sql = "select `festival` from `festival_monitor` where user=? and deleted!='1'";
        Integer temp = user.id;
        String[] args = {temp.toString()};
        JSONArray fests = new JSONArray();
        try {
            JSONArray rawUsers = DBConnect.dbQuery(sql, args);
            for (int i = 0; i < rawUsers.length(); i++) fests.put(i, rawUsers.getJSONObject(i).getInt("festival"));
        } catch (SQLException | JSONException e) {
            e.printStackTrace();
        }
        return fests;
    }

    public static JSONArray getAllFestivals() {
        String sql = "select `id` from `festivals` where deleted!='1'";
        String[] args = {};
        JSONArray fests = new JSONArray();
        try {
            JSONArray rawUsers = DBConnect.dbQuery(sql, args);
            for (int i = 0; i < rawUsers.length(); i++) fests.put(i, rawUsers.getJSONObject(i).getInt("id"));
        } catch (SQLException | JSONException e) {
            e.printStackTrace();
        }
        return fests;
    }

    public static JSONArray getUnpurchasedFestivals(User user) {
        JSONArray fests = new JSONArray();
        JSONArray purchased = getPurchasedFestivals(user);
        JSONArray all = getAllFestivals();
        try {
            for (int i = 0; i < all.length(); i++) {
                int j;
                for (j = 0; j < purchased.length(); j++) {
                    if (all.getInt(i) == purchased.getInt(j)) break;
                }
                if (j >= purchased.length())
                    fests.put(all.getInt(i));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return fests;
    }

    protected static JSONArray getFestivalDays(Festival fest) {
        String sql = "select `id` from `days` where `festival`=? and deleted!='1'";
        String[] args = {Integer.toString(fest.id)};
        JSONArray fests = new JSONArray();
        try {
            JSONArray rawUsers = DBConnect.dbQuery(sql, args);
            for (int i = 0; i < rawUsers.length(); i++) fests.put(i, rawUsers.getJSONObject(i).getInt("id"));
        } catch (SQLException | JSONException e) {
            e.printStackTrace();
        }
        return fests;
    }

    protected void setFields(JSONArray resultArray) throws JSONException {
        JSONObject rs;
        rs = resultArray.getJSONObject(0);
        name = rs.getString("name");
        dbname = rs.getString("dbname");
        sitename = rs.getString("sitename");
        website = rs.getString("website");
        description = rs.getString("description");
        id = rs.getInt("id");
        year = rs.getString("year");
        mode = rs.getInt("mode");
        series = new FestivalSeries(rs.getInt("series"));
        start_time = rs.getInt("start_time");
        length = rs.getInt("length");
        cost = rs.getInt("cost");
        num_days = rs.getInt("num_days");
        num_dates = rs.getInt("num_dates");
        System.err.println("Fest instantiation: setting fields");

        JSONArray dateIDs = getFestivalDates();
        dates = new ArrayList<>();
        System.err.println("Fest instantiation: date IDs gotten: " + dateIDs.toString());
        for (int i = 0; i < dateIDs.length(); i++) dates.add(new FestivalDate(dateIDs.getInt(i)));

        System.err.println("Fest instantiation: still setting fields");


    }

    public JSONObject getAppData(User self, Festival fest) throws JSONException {
        System.out.println("Starting data creation");
        JSONObject appData = new JSONObject();
        appData.put("sitename", fest.sitename);
        appData.put("name", fest.name);
        appData.put("dbname", fest.dbname);
        appData.put("website", fest.website);
        appData.put("description", fest.description);
        appData.put("id", fest.id);
        appData.put("year", fest.year);
        appData.put("mode", fest.mode);
        appData.put("seriesName", fest.series.name);
        appData.put("seriesDescription", fest.series.description);
        appData.put("seriesID", fest.series.id);
        appData.put("start_time", fest.start_time);
        appData.put("length", fest.length);
        appData.put("cost", fest.cost);
        System.out.println("Cost data created");
        appData.put("seriesData", fest.series.getAppData(self, fest));
        System.out.println("Series data created");
        JSONArray dateData = new JSONArray();
        for (FestivalDate date : fest.dates) {
            JSONObject tempDate = date.getAppData(self, fest);
            System.out.println("Date created: " + tempDate.getInt("id"));
            dateData.put(tempDate);
        }
        System.out.println("Date data created");
        appData.put("dateData", dateData);
        return appData;
    }

    protected JSONArray getFestivalDates() {
        String sql = "select `id` from `dates` where `festival`=? and deleted!='1'";
        String[] args = {Integer.toString(id)};
        JSONArray fests = new JSONArray();
        try {
            JSONArray rawUsers = DBConnect.dbQuery(sql, args);
            for (int i = 0; i < rawUsers.length(); i++) fests.put(i, rawUsers.getJSONObject(i).getInt("id"));
        } catch (SQLException | JSONException e) {
            e.printStackTrace();
        }
        return fests;
    }

    class FestivalSeries extends FestivalTimeObject {
        static final String CREATE_SQL = "select `id`, `name`, `description` " +
                "from festival_series " +
                "where `id`=? and deleted!='1';";
        int id;
        String name, description;

        FestivalSeries(int idS) throws JSONException {
            super(idS, CREATE_SQL);
        }

        protected void setFields(JSONArray resultArray) throws JSONException {
            JSONObject rs;
            rs = resultArray.getJSONObject(0);
            name = rs.getString("name");
            description = rs.getString("description");
            id = rs.getInt("id");

        }

        protected JSONObject getAppData(User self, Festival fest) throws JSONException {
            JSONObject appData = new JSONObject();
            appData.put("id", id);
            appData.put("name", name);
            appData.put("description", description);
            return appData;
        }
    }

    class FestivalDate extends FestivalTimeObject {
        static final String CREATE_SQL = "select `id`, `name`, " +
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
            System.err.println("Fest instantiation: about to get venue ");
            venue = new Venue(rs.getInt("venue"));
            Date tempDate = (Date) rs.get("basedate");
            LocalDate localDate = LocalDate.fromDateFields(tempDate);
            baseDate = localDate.toDateTimeAtStartOfDay(venue.timeZone);
            JSONArray dayIDs = Festival.getFestivalDays(Festival.this);
            days = new ArrayList<>();
//            System.err.println("Fest instantiation: day IDs gotten: " + dayIDs.toString());
            for (int i = 0; i < dayIDs.length(); i++) {
                Day tempDay = new Day(dayIDs.getInt(i));
//                System.err.println("Fest instantiation: day created: " + Integer.toString(tempDay.id));
                days.add(tempDay);
            }
//            System.err.println("Fest instantiation: field set for date: "+Integer.toString(id));

        }

        JSONObject getAppData(User self, Festival fest) throws JSONException {
            JSONObject appData = new JSONObject();
            appData.put("id", id);
            appData.put("name", name);
            System.err.println("Collecting data: venue not yet set for date: " + Integer.toString(id));
            appData.put("venueData", venue.getAppData(self, fest));
            JSONArray dayData = new JSONArray();
            System.err.println("Collecting data: field set for date: " + Integer.toString(id));
            for (Day day : days) dayData.put(day.getAppData(self, fest));
            appData.put("dayData", dayData);
            return appData;
        }
    }

    class Day extends FestivalTimeObject {
        static final String CREATE_SQL = "select `id`, `name`, " +
                "`days_offset` " +
                "from days " +
                "where `id`=? and deleted!='1';";
        int id, offset;
        String name, description;

        Day(int idS) throws JSONException {
            super(idS, CREATE_SQL);
        }

        protected void setFields(JSONArray resultArray) throws JSONException {
            JSONObject rs;
            rs = resultArray.getJSONObject(0);
            name = rs.getString("name");
            id = rs.getInt("id");
            offset = rs.getInt("days_offset");
            System.err.println("Fest instantiation: setting fields for day: " + Integer.toString(id));


        }

        JSONObject getAppData(User self, Festival fest) throws JSONException {
            JSONObject appData = new JSONObject();
            appData.put("id", id);
            appData.put("offset", offset);
            appData.put("name", name);
            appData.put("description", description);
            return appData;
        }
    }

}
