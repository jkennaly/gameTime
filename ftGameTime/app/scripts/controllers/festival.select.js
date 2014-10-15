/**
 * Created by jbk on 10/5/14.
 */

/**
 * @ngdoc function
 * @name ftGameTimeApp.controller:FestivalSelectCtrl
 * @description
 * # FestivalSelectCtrl
 * Controller of the ftGameTimeApp
 */
angular.module('ftGameTimeApp')
    .controller('FestivalSelectCtrl', ['$scope', '$location', 'Objectify', 'AppServer', function ($scope, $location, Objectify, AppServer) {
        $scope.festivals = {};
        $scope.festivals.selected = null;
        $scope.festivals.purchased = Objectify.result("purchasedFestivals");
        $scope.festivals.unpurchased = Objectify.result("unpurchasedFestivals");

        $scope.festivals.chooseFest = function () {

            var reqType;
            reqType = $scope.festivals.purchased.indexOf($scope.festivals.selected) > -1 ? "select_purchased" : "select_unpurchased";

//        alert("Select Purchased Festival: " + form.select_purchased);
            // submit a festival to receive the gametime data
            var data = {
                reqType: reqType,
                selectedFestival: $scope.festivals.selected.id
            };
            console.log($scope.festivals.selected);
            var reqChallenge = AppServer.request(data).response;

            reqChallenge.success(function () {
                $location.path("/festival/home");
            }).error(function () {
                $location.path("/login/failed/network");
            });
            return false;
        }

    }]);
