/**
 * Created by jbk on 10/9/14.
 */

angular.module('ftGameTimeApp')
    .factory('FestivalSets', function FestivalSetsFactory(Objectify) {
        var fBands = Objectify.result("setFestivalData");
        var bandIds = Object.keys(fBands);
        return bandIds;

    });