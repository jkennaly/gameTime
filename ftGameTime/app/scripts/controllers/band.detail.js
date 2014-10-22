/**
 * Created by jbk on 10/5/14.
 */
'use strict';
/**
 * @ngdoc function
 * @name ftGameTimeApp.controller:BandDetailCtrl
 * @description
 * # BandDetailCtrl
 * Controller of the ftGameTimeApp
 */
angular.module('ftGameTimeApp')
    .controller('BandDetailCtrl', function ($scope, $stateParams, Band, FestivalSets, FestivalFestival) {
//        console.log("Band Ctrl");
        $scope.band = {};
        $scope.festival = {};
        $scope.band.current = new Band($stateParams.bandID);
        $scope.bandSets = FestivalSets;
        $scope.festival.current = FestivalFestival;
        $scope.bandSet = function (set) {
            /*
             console.log(set);
             console.log($scope.band.current.id);
             if($scope.band.current.id == set.band ) console.log(set);
             */
            return $scope.band.current.id == set.band;
        }
//        console.log("start time", $scope.bandSets[0].startTime);

        var len = $scope.band.current.cardData.length;
        while (len--) {
            if ($scope.band.current.cardData[len].festival === FestivalFestival.sitename) {
                $scope.band.current.cardData.splice(len, 1);
            }
        }


    });
