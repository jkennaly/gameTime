/**
 * Created by jbk on 10/9/14.
 */

angular.module('ftGameTimeApp')
    .factory('Set', function (Objectify) {


        var setFestivalData = Objectify.result("setFestivalData");
        // instantiate our initial object
        var Set = function (id) {
            this.id = id;
            this.startTime = new Date(setFestivalData[id].startTime);
            this.endTime = new Date(setFestivalData[id].endTime);
            this.band = setFestivalData[id].band;
            this.day = setFestivalData[id].day;
            this.stage = setFestivalData[id].stage;

        };
        Set.getBandSets = function (band) {
            var setFestivalData = Objectify.result("setFestivalData");
            console.log(setFestivalData);
            var sets = [];
            for (var set in setFestivalData) {
                if (set.band == band) sets.push(set);
            }
            return sets;
        }

        return Set;
    });