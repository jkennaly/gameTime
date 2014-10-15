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
    .controller('BandDetailCtrl', function ($scope, $routeParams, Band) {
        $scope.band = new Band($routeParams.bandID);


    });
