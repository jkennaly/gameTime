/**
 * Created by jbk on 10/9/14.
 */

angular.module('ftGameTimeApp')
    .factory('Day', function (Objectify) {


        var dayFestivalData = Objectify.result("dayFestivalData");
        // instantiate our initial object
        var Day = function (id) {
            this.id = id;
            this.name = dayFestivalData[id].name;
            this.offset = dayFestivalData[id].offset;
        };

        return Day;
    });