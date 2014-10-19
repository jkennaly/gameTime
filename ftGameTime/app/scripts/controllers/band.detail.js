/**
 * Created by jbk on 10/5/14.
 */

/**
 * @ngdoc function
 * @name ftGameTimeApp.controller:BandDetailCtrl
 * @description
 * # BandDetailCtrl
 * Controller of the ftGameTimeApp
 */
angular.module('ftGameTimeApp')
    .controller('BandDetailCtrl', function ($scope, $stateParams, Band, FestivalFestival) {
        $scope.band = {};
        $scope.festival = {};
        $scope.band.current = new Band($stateParams.bandID);
        $scope.bandSets = $scope.band.current.getSets();
        $scope.festival.current = FestivalFestival;

        var len = $scope.band.current.cardData.length;
        while (len--) {
            if ($scope.band.current.cardData[len].festival == FestivalFestival.sitename) {
                $scope.band.current.cardData.splice(len, 1);
            }
        }


    });
