/**
 * Created by jbk on 10/9/14.
 */

angular.module('ftGameTimeApp')
    .factory('FestivalDate', function (Objectify) {
        return Objectify.result("dateFestivalData");
    });