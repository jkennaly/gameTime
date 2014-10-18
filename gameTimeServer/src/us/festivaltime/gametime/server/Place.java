package us.festivaltime.gametime.server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    @Override
    void setFields(JSONArray resultArray) throws JSONException {

    }

    JSONObject getAppData(User self, Festival fest) throws JSONException {
        return null;
    }
}
