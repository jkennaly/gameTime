package us.festivaltime.gametime.server;

import org.json.JSONArray;
import org.json.JSONException;

import java.sql.SQLException;

/**
 * Created by jbk on 9/26/14.
 */
public abstract class FestivalTimeObject {
    int id;

    protected FestivalTimeObject(int id, String createSQL) throws JSONException {
        this.id = id;
        String[] args = {Integer.toString(id)};
        JSONArray resultArray = new JSONArray();

        try {
            resultArray = DBConnect.dbQuery(createSQL, args);
        } catch (JSONException | SQLException e) {
            e.printStackTrace();
        }

/*
        if (resultArray.length() != 1) {
            try {
                throw new InstantiationException("The username or email address was not valid");
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        } else {
        */
        setFields(resultArray);


    }

    public FestivalTimeObject() {
        super();
    }

    abstract void setFields(JSONArray resultArray) throws JSONException;


}
