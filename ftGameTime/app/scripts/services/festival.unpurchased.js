/**
 * Created by jbk on 10/9/14.
 */

angular.module('ftGameTimeApp')
    .factory('FestivalUnpurchased', function (Objectify) {
        return Objectify.result("unpurchasedFestivals");
    });