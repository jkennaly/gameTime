/**
 * Created by jbk on 10/9/14.
 */

angular.module('ftGameTimeApp')
    .factory('FestivalSelf', function (Objectify) {
        return Objectify.result("selfData");
    });