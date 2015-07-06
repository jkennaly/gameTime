package us.festivaltime.gametime.server;

import org.intellij.lang.annotations.Language;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jbk on 10/10/14.
 */

public class Set extends FestivalTimeObject {
    static final String CREATE_SQL = "SELECT `id`, `festival`, `band`, `day`, `stage`, `date`, `start`, `end`, " +
            "count(*) AS total_rows FROM `sets` WHERE `deleted`!='1' AND (`id`=?)";
    int id;
    int bandPriority;
    Band band;
    List<Band> specialGuest;
    FestivalDate festivalDate;
    Day festivalDay;
    Place stage;
    Festival festival;

    DateTime startTime, endTime;

    public Set(int idS) throws JSONException {

        super(idS, CREATE_SQL);

    }

    public Set(int idS, FestivalDate festivalDate, Festival festival){
        this.id = idS;
        this.festivalDate = festivalDate;
        this.festival = festival;

        String[] args = {Integer.toString(id)};
        JSONArray resultArray;
        JSONObject rs;
        int sec = 0;

        try {
            resultArray = DBConnect.dbQuery(CREATE_SQL, args);
            rs = resultArray.getJSONObject(0);
            festivalDay = new Day(rs.getInt("day"));
            stage = new Place(rs.getInt("stage"));
            band = new Band(rs.getInt("band"));
            sec = rs.getInt("start");
            startTime = this.festivalDate.baseDate;
//        System.out.println("baseDate (sets): " + startTime.getMillis() );
            startTime = startTime.plusDays(festivalDay.offset);
//        System.out.println("baseDate (sets) days offset: " + startTime.getMillis() );
            startTime = startTime.plusSeconds(festival.start_time);
//        System.out.println("baseDate (sets) days offset: " + startTime.getMillis() );
            startTime = startTime.plusSeconds(sec);
//        System.out.println("baseDate (sets) start: " + startTime.getMillis() );
            endTime = this.festivalDate.baseDate.plusDays(festivalDay.offset).plusSeconds(festival.start_time).plusSeconds(rs.getInt("end"));

        } catch (JSONException | SQLException e) {
            e.printStackTrace();
        }



        specialGuest = null;

        String sql = "select `id`, `priority` from `band_list` where `festival`=? and `band`=? and deleted!='1';";

        String[] args2 = {Integer.toString(festival.id), Integer.toString(band.id)};
        try {
            //     System.out.println("sql: " + sql);
            JSONArray qResult = DBConnect.dbQuery(sql, args2);
            bandPriority = qResult.getJSONObject(0).getInt("priority");
            //        System.out.println("base Date: " + festivalDate.baseDate.toString());


        } catch (SQLException | JSONException e) {
            e.printStackTrace();
        }
        //calculate the start time by adding the Date basedate, the day days_offset, and the start value



    }

    static List<Set> getAllSets(Band band) {
        String sql = "select `id` from `sets` where deleted!='1' and `band`=?";
        String[] args = {Integer.toString(band.id)};
        List<Set> sets = new ArrayList<>();
//        System.err.println("Fest instantiation: date IDs gotten: " + dateIDs.toString());
        try {
            JSONArray setIDs = DBConnect.dbQuery(sql, args);
            for (int i = 0; i < setIDs.length(); i++) sets.add(new Set(setIDs.getInt(i)));

        } catch (SQLException | JSONException e) {
            e.printStackTrace();
        }
        return sets;
    }

    static List<Set> getAllDateSets(FestivalDate date, Festival festival) {
        String sql = "select `id` from `sets` where `deleted`!='1' and `date`=?";
        String[] args = {Integer.toString(date.id)};
        List<Set> sets = new ArrayList<>();
//        System.err.println("Fest instantiation: date IDs gotten: " + dateIDs.toString());
        try {
            JSONArray setIDs = DBConnect.dbQuery(sql, args);
            for (int i = 0; i < setIDs.length(); i++) sets.add(new Set(setIDs.getJSONObject(i).getInt("id"), date, festival));

        } catch (SQLException | JSONException e) {
            e.printStackTrace();
        }
        return sets;
    }

    static List<Set> getAllRatedSets(Band band) {
        @Language("MySQL") String sql = "SELECT DISTINCT `set` FROM `messages` WHERE deleted!='1' AND `band`=? AND `mode`='2' AND `remark`='2'";
        String[] args = {Integer.toString(band.id)};
        List<Set> sets = new ArrayList<>();
        try {
            JSONArray setIDs = DBConnect.dbQuery(sql, args);
//            System.err.println("Fest instantiation: set IDs gotten: " + setIDs.toString());
            for (int i = 0; i < setIDs.length(); i++) sets.add(new Set(setIDs.getJSONObject(i).getInt("set")));

        } catch (SQLException | JSONException e) {
            e.printStackTrace();
        }
        return sets;
    }

    static JSONArray getAppData(FestivalDate date, Festival festival) throws JSONException {
        JSONArray appData = new JSONArray();
        List<Set> sets = getAllDateSets(date, festival);
        for (Set set : sets) {
            JSONObject tempData = new JSONObject();
            tempData.put("id", set.id);
            tempData.put("band", set.band.id);
            tempData.put("day", set.festivalDay.id);
            tempData.put("stage", set.stage.id);
            tempData.put("startTime", set.startTime.getMillis());
            tempData.put("endTime", set.endTime.getMillis());
            appData.put(tempData);
        }

        return appData;
    }

    @Override
    void setFields(JSONArray resultArray) throws JSONException {
        JSONObject rs;
        rs = resultArray.getJSONObject(0);
        id = rs.getInt("id");

        System.out.println("Festival id: " + rs.getInt("festival"));
        System.out.println("Date id: " + rs.getInt("date"));
        festival = new Festival(rs.getInt("festival"));


        festivalDay = new Day(rs.getInt("day"));
        stage = new Place(rs.getInt("stage"));
        specialGuest = null;
        band = new Band(rs.getInt("band"));

        String sql = "select `id`, `priority` from `band_list` where `festival`=? and `band`=? and deleted!='1';";

        String[] args = {Integer.toString(festival.id), Integer.toString(band.id)};
        try {
            //     System.out.println("sql: " + sql);
            JSONArray qResult = DBConnect.dbQuery(sql, args);
            bandPriority = qResult.getJSONObject(0).getInt("priority");

        } catch (SQLException | JSONException e) {
            e.printStackTrace();
        }
        //calculate the start time by adding the Date basedate, the day days_offset, and the start value

        int sec = rs.getInt("start");
        if (rs.getInt("date") > 0) {
            festivalDate = new FestivalDate(rs.getInt("date"));
//        System.out.println("base Date: " + festivalDate.baseDate.toString());
            startTime=festivalDate.baseDate;
//        System.out.println("baseDate (sets): " + startTime.getMillis() );
            startTime=startTime.plusDays(festivalDay.offset);
//        System.out.println("baseDate (sets) days offset: " + startTime.getMillis() );
            startTime=startTime.plusSeconds(festival.start_time);
//        System.out.println("baseDate (sets) days offset: " + startTime.getMillis() );
            startTime=startTime.plusSeconds(sec);
//        System.out.println("baseDate (sets) start: " + startTime.getMillis() );
            endTime=festivalDate.baseDate.plusDays(festivalDay.offset).plusSeconds(festival.start_time).plusSeconds(rs.getInt("end")

            );
        }
        else {
            festivalDate = null;
//        System.out.println("base Date: " + festivalDate.baseDate.toString());
            startTime=  null;
            endTime = null;
        }
        }

}
