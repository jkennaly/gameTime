/**
 * Created by jbk on 10/9/14.
 */

angular.module('ftGameTimeApp')
    .factory('FestivalMessage', function (Objectify) {
        return Objectify.result("messageFestivalData");
    });