package us.festivaltime.gametime.server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jbk on 10/10/14.
 */

public class Comment extends Remark {
    @Override
    void setFields(JSONArray resultArray) throws JSONException {

    }

    @Override
    JSONObject getAppData(User self, Festival fest) throws JSONException {
        return null;
    }
}
