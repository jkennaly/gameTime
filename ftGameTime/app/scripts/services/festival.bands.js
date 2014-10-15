/**
 * Created by jbk on 10/9/14.
 */

angular.module('ftGameTimeApp')
    .factory('FestivalBands', function (Objectify) {
        var fBands = Objectify.result("bandFestivalData");
        var bandIds = Object.keys(fBands);
        return bandIds;

    });