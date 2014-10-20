/**
 * Created by jbk on 10/5/14.
 */

/**
 * @ngdoc function
 * @name ftGameTimeApp.controller:BandOverviewCtrl
 * @description
 * # BandOverviewCtrl
 * Controller of the ftGameTimeApp
 */
angular.module('ftGameTimeApp')
    .controller('BandOverviewCtrl', function ($scope, FestivalBands, Band) {

        /* $scope.bandArray = [];

         for (var i = 0; i < FestivalBands.length; i++) {
         //            console.log("i: " + i + "FestivalBands[i]" + FestivalBands[i]);
         $scope.bandArray[i] = new Band(FestivalBands[i]);
         }*/

        $scope.bandIds = FestivalBands;
    });
