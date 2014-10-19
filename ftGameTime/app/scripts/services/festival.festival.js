/**
 * Created by jbk on 10/9/14.
 */

angular.module('ftGameTimeApp')
    .factory('FestivalFestival', function (Objectify) {
        return Objectify.result("currentFestivalData");
    });