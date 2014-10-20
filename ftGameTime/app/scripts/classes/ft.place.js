/**
 * Created by jbk on 10/9/14.
 */

angular.module('ftGameTimeApp')
    .factory('Place', function (Objectify) {


        var placeFestivalData = Objectify.result("placeFestivalData");
        // instantiate our initial object
        var Place = function (id) {
            this.id = id;
            this.name = placeFestivalData[id].name;
            this.type = placeFestivalData[id].type;
            this.priority = placeFestivalData[id].priority;
            this.layout = placeFestivalData[id].layout;
        };

        return Place;
    });