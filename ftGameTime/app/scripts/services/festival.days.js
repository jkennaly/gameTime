/**
 * Created by jbk on 10/9/14.
 */

angular.module('ftGameTimeApp')
    .factory('FestivalDays', function (Objectify) {
        var fBands = Objectify.result("dayFestivalData");
        var bandIds = Object.keys(fBands);
        return bandIds;

    });