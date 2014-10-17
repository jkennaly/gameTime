/**
 * Created by jbk on 10/9/14.
 */

angular.module('ftGameTimeApp')
    .factory('FestivalPurchased', function (Objectify) {
        return Objectify.result("purchasedFestivals");
    });