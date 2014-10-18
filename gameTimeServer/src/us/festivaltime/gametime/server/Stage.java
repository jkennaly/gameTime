package us.festivaltime.gametime.server;

import org.json.JSONException;

/**
 * Created by jbk on 10/10/14.
 */

public class Stage extends Place {
    static final String CREATE_SQL = "select `id`, `name`, " +
            "`layout`, `priority` " +
            "from `places` " +
            "where `id`=? and `type`='1' and deleted!='1';";
    int id, layout, priority;
    String name;

    public Stage(int idS) throws JSONException {

        super(idS);

    }

}
