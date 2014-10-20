/**
 * Created by jbk on 10/9/14.
 */

angular.module('ftGameTimeApp')
    .factory('FestivalPlaces', function (Objectify) {
        var fBands = Objectify.result("placeFestivalData");
        var bandIds = Object.keys(fBands);
        return bandIds;

    });