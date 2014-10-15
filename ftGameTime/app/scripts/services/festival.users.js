/**
 * Created by jbk on 10/9/14.
 */

angular.module('ftGameTimeApp')
    .factory('FestivalUsers', function (Objectify) {
        var fBands = Objectify.result("userFestivalData");
        var bandIds = Object.keys(fBands);
        return bandIds;

    });