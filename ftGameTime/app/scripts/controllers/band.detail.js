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
    .controller('BandDetailCtrl', function ($scope, $routeParams, Band, Set) {
        $scope.band = {};
        $scope.band.current = new Band($routeParams.bandID);
        $scope.bandSets = $scope.band.current.getSets();
        console.log($scope.bandSets);


    })
    .controller('CardDataCtrl', function ($scope, User) {
        $scope.reportUserImage = function (reportAuthor) {
            var user = new User(reportAuthor);
            return user.imageSrc;
        }
        $scope.reportUserName = function (reportAuthor) {
            var user = new User(reportAuthor);
            return user.username;
        }
    });
