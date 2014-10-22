/**
 * Created by jbk on 10/9/14.
 */

angular.module('ftGameTimeApp')
    .factory('FestivalSets', function FestivalSetsFactory(Objectify) {
        return Objectify.result("setFestivalData");
    });