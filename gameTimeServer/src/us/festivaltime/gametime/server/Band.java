package us.festivaltime.gametime.server;

import org.intellij.lang.annotations.Language;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jbk on 10/9/14.
 */

public class Band extends FestivalTimeObject {

    static final String CREATE_SQL = "SELECT `id`, `name`, " +
            "count(*) AS total_rows FROM `bands` WHERE `deleted`!='1' AND (`id`=?)";
    String name;
    int id;

    public Band(int idS) throws JSONException {
        super(idS, CREATE_SQL);


    }

    static JSONObject getAppData(User self, Festival fest) throws JSONException {
        JSONObject appData = new JSONObject();
        long time = System.currentTimeMillis();
        appData.put("appDataTime", time);
        List<Band> bands = getAllBands(fest);
        for (Band band : bands) {
            //           System.out.println("getting data for: " + band.name);
            JSONObject bandData = new JSONObject();
            bandData.put("id", band.id);
            bandData.put("name", band.name);
            bandData.put("genre", band.getGenre(self));
            bandData.put("priority", band.getPriority(fest));
            bandData.put("parentGenre", band.getParentGenre(self));
            bandData.put("imageSrc", band.getImageString());
            JSONObject ratings = new JSONObject();
            //           System.out.println("getting ratings for: " + band.name);
            ratings.put("userPgRating", band.getUsersRating(self, fest, 1));
//            System.out.println("got a ratings for: " + band.name);
            ratings.put("userGtRating", band.getUsersRating(self, fest, 2));
            ratings.put("followedPgRating", band.getFollowedsRating(self, fest, 1));
            ratings.put("followedGtRating", band.getFollowedsRating(self, fest, 2));
            //           System.out.println("got some ratings for: " + band.name);
            ratings.put("sitesPgRating", band.getSitesRating(fest, 1));
            ratings.put("sitesGtRating", band.getSitesRating(fest, 2));
            ratings.put("siteAtPgRating", band.getSitesRating(1));
            ratings.put("siteAtGtRating", band.getSitesRating(2));
//            System.out.println("got all ratings for: " + band.name);
            ratings.put("numCheckIns", band.numberCheckins());
//            System.out.println("getting card data for: " + band.name);
            bandData.put("ratingsData", ratings);
            bandData.put("cardData", band.getCardData(self));
            appData.put(String.valueOf(band.id), bandData);
//            System.out.println("appData complete for: " + band.name);
        }


        return appData;
    }

    static List<Band> getAllBands(Festival fest) {
        String sql = "select distinct `band` from `band_list` where deleted!='1' and `festival`=?";
        String[] args = {Integer.toString(fest.id)};
        List<Band> bands = new ArrayList<>();
//        System.err.println("Fest instantiation: date IDs gotten: " + dateIDs.toString());
        try {
            JSONArray bandIDs = DBConnect.dbQuery(sql, args);
            for (int i = 0; i < bandIDs.length(); i++) bands.add(new Band(bandIDs.getJSONObject(i).getInt("band")));

        } catch (SQLException | JSONException e) {
            e.printStackTrace();
        }
        return bands;
    }

    static int getParentGenre(int genreId) {
        int genre = 98;
        String sql = "select `parent` from `genres` where `id`=? AND `deleted`!='1'";

        String[] args = {Integer.toString(genreId)};
        JSONArray genreRes;
        try {
            genreRes = DBConnect.dbQuery(sql, args);
            for (int i = 0; i < genreRes.length(); i++) genre = genreRes.getJSONObject(i).getInt("parent");
        } catch (SQLException | JSONException e) {
            e.printStackTrace();
        }
        return genre;
    }

    int getPriority(Festival fest) {
        int priority = 98;
        String sql = "select `priority` from `band_list` where `band`=? AND `festival`=? AND `deleted`!='1'";

        String[] args = {Integer.toString(id), Integer.toString(fest.id)};

        JSONArray genreRes;
        try {
            genreRes = DBConnect.dbQuery(sql, args);
            priority = genreRes.getJSONObject(0).getInt("priority");
        } catch (SQLException | JSONException e) {
            e.printStackTrace();
        }
        return priority;
    }

    private String getImageString() {
        return "https://www.festivaltime.us/includes/content/blocks/getPicture4.php?band=" + id;

    }

    @Override
    void setFields(JSONArray resultArray) throws JSONException {
        JSONObject rs;
        rs = resultArray.getJSONObject(0);
        name = rs.getString("name");
        id = rs.getInt("id");
    }

    /*
    all gt checkins detail (via cards)
    Band pic
     */
    int getUsersRating(User user, Festival fest, int mode) {
        //mode 0 = all modes
        //mode 1 = pregame
        //mode 2 = gametime
        String intString = "";
        String modeString;
        if (mode == 0) modeString = "`mode`!='0'";
        else modeString = "`mode`='" + mode + "'";
        String sql = "SELECT `content` as rating FROM `messages` as rating " +
                "WHERE `band`=? and `fromuser`=? and `remark`='2' " +
                "and " + modeString + " and `festival`=? and `deleted`!='1'";
        String[] args = {Integer.toString(id), Integer.toString(user.id), Integer.toString(fest.id)};
        JSONArray genreRes;
        try {
            genreRes = DBConnect.dbQuery(sql, args);
            for (int i = 0; i < genreRes.length(); i++) intString = genreRes.getJSONObject(i).getString("rating");
        } catch (SQLException | JSONException e) {
            e.printStackTrace();
        }
        try {
            return Integer.parseInt(intString);
        } catch (NumberFormatException nfe) {
            return 0;
        }
    }

    double getFollowedsRating(User user, Festival fest, int mode) {
        //mode 0 = all modes
        //mode 1 = pregame
        //mode 2 = gametime
        String modeString;
        if (mode == 0) modeString = "`mode`!='0'";
        else modeString = "`mode`='" + mode + "'";
        Double res = null;
        String sql = "SELECT AVG(`content`) as rating FROM `messages` " +
                "WHERE `band`=? and `remark`='2' " +
                "and " + modeString + " and `festival`=? and `deleted`!='1' and (";
        boolean set = false;
        for (int follow : user.follows) {
            if (set) sql = sql + "or `fromuser`='" + follow + "' ";
            else sql = sql + "`fromuser`='" + follow + "' ";
            set = true;
        }
        sql = sql + ")";
        String[] args = {Integer.toString(id), Integer.toString(fest.id)};
        JSONArray genreRes;
        try {
            genreRes = DBConnect.dbQuery(sql, args);
            for (int i = 0; i < genreRes.length(); i++) res = genreRes.getJSONObject(i).getDouble("rating");
        } catch (SQLException | JSONException e) {
            e.printStackTrace();
        }

        return res;
    }

    double getSitesRating(Festival fest, int mode) {
        //mode 0 = all modes
        //mode 1 = pregame
        //mode 2 = gametime
        Double res = null;
        String modeString;
        if (mode == 0) modeString = "`mode`!='0'";
        else modeString = "`mode`='" + mode + "'";
        String sql = "SELECT AVG(`content`) as rating FROM `messages` " +
                "WHERE `band`=? and `remark`='2' " +
                "and " + modeString + " and `festival`=? and `deleted`!='1'";
        String[] args = {Integer.toString(id), Integer.toString(fest.id)};
        JSONArray genreRes;
        try {
            genreRes = DBConnect.dbQuery(sql, args);
            if (genreRes.length() > 0)
                for (int i = 0; i < genreRes.length(); i++) res = genreRes.getJSONObject(i).getDouble("rating");
        } catch (SQLException | JSONException e) {
            e.printStackTrace();
        }

        return res;
    }

    double getSitesRating(int mode) {
        //mode 0 = all modes
        //mode 1 = pregame
        //mode 2 = gametime
        Double res = null;
        String modeString;
        if (mode == 0) modeString = "`mode`!='0'";
        else modeString = "`mode`='" + mode + "'";
        String sql = "SELECT AVG(`content`) as rating FROM `messages` " +
                "WHERE `band`=? and `remark`='2' " +
                "and " + modeString + " and `deleted`!='1'";
        String[] args = {Integer.toString(id)};
        JSONArray genreRes;
        try {
            genreRes = DBConnect.dbQuery(sql, args);
            if (genreRes.length() > 0)
                for (int i = 0; i < genreRes.length(); i++) res = genreRes.getJSONObject(i).getDouble("rating");

        } catch (SQLException | JSONException e) {
            e.printStackTrace();
        }

        return res;
    }

    int numberCheckins() {
        int intString = 0;
        String sql = "SELECT COUNT(sets.`id`) as checkins from sets, messages " +
                "where sets.festival=messages.festival and sets.band=messages.band and " +
                "sets.band=? and sets.day=messages.day and sets.stage=messages.location group by sets.`id`;";
        String[] args = {Integer.toString(id)};
        JSONArray genreRes;
        try {
            genreRes = DBConnect.dbQuery(sql, args);
            for (int i = 0; i < genreRes.length(); i++) intString = genreRes.getJSONObject(i).getInt("checkins");
        } catch (SQLException | JSONException e) {
            e.printStackTrace();
        }

        return intString;
    }

    JSONArray getCardData(User self) throws JSONException {
        /*
        A card is the information from a user who checked in to a band
        It's a quick way to see what people have thught about that band
        Card data:
        A user
        A festival
        Thier pregame rating (if any)
        Their gametime rating (if any)
        Their pregame comments (if any)
        Their gametime comments associated to the set (if any)
        */

        JSONArray ja = new JSONArray();
        List<Set> ratedSets = Set.getAllRatedSets(this);
        List<User> visibleUsers = new ArrayList<>();
        JSONArray userIds = self.getVisibleUsers();
//        System.out.println("Initial card data collected");
        for (int i = 0; i < userIds.length(); i++) visibleUsers.add(new User(userIds.getInt(i)));
        for (User user : visibleUsers) {
            for (Set set : ratedSets) {
//                System.out.println("Checking set " + set.id);
                if (Message.userRatedSet(user, set)) {
                    JSONObject showReport = new JSONObject();
                    showReport.put("reportAuthor", user.id);
                    showReport.put("setId", set.id);
                    showReport.put("festival", set.festival.sitename);
//                    System.out.println("Grabbing data for set " + set.id);
                    showReport.put("pgRating", Message.userPgRating(user, set.band, set.festival));
                    showReport.put("gtRating", Message.userGtRating(user, set));
                    showReport.put("pgComment", Message.userPgComment(user, set.band, set.festival));
                    showReport.put("gtComment", Message.userGtComment(user, set));
                    ja.put(showReport);
                }

            }
        }


        return ja;
    }

    String getGenre(User user) {
        String genre = "";
        int genreId = getGenreId(user);
        String sql = "select `name` from `genres` where `id`=? AND `deleted`!='1'";
        String[] args = {Integer.toString(genreId)};
        JSONArray genreRes;
        try {
            genreRes = DBConnect.dbQuery(sql, args);
            for (int i = 0; i < genreRes.length(); i++) genre = genreRes.getJSONObject(i).getString("name");
        } catch (SQLException | JSONException e) {
            e.printStackTrace();
        }
        return genre;
    }

    int getGenreId(User user) {
        String[] args;
        int genreId = 0;

        @Language("MySQL") String sql = "SELECT genre FROM bandgenres WHERE band=? AND user=?";

        args = new String[]{Integer.toString(id), Integer.toString(user.id)};
        try {
            JSONArray userGenre = DBConnect.dbQuery(sql, args);

            if (userGenre.length() == 0) {
                sql = "SELECT `genre`, count(`user`) AS num FROM `bandgenres` WHERE `band`=? GROUP BY genre ORDER BY num DESC LIMIT 1";
                args = new String[]{Integer.toString(id)};
                JSONArray otherGenre = DBConnect.dbQuery(sql, args);
                if (otherGenre.length() != 0) genreId = otherGenre.getJSONObject(0).getInt("genre");
            } else genreId = userGenre.getJSONObject(0).getInt("genre");
        } catch (SQLException | JSONException e) {
            e.printStackTrace();
        }
        int genreSetting = user.getSetting(73);
        if (genreSetting != 2) genreId = getParentGenre(genreId);

        return genreId;
    }

    int getParentGenre(User user) {
        int genreId, genre = 0;
        genreId = getGenreId(user);
        String sql = "select `parent` from `genres` where `id`=? AND `deleted`!='1'";

        String[] args = {Integer.toString(genreId)};
        JSONArray genreRes;
        try {
            genreRes = DBConnect.dbQuery(sql, args);
            for (int i = 0; i < genreRes.length(); i++) genre = genreRes.getJSONObject(i).getInt("parent");
        } catch (SQLException | JSONException e) {
            e.printStackTrace();
        }
        return genre;
    }


}
