/**
 * Created by jbk on 10/5/14.
 */

/**
 * @ngdoc function
 * @name ftGameTimeApp.controller:UserOverviewCtrl
 * @description
 * # UserOverviewCtrl
 * Controller of the ftGameTimeApp
 */
angular.module('ftGameTimeApp')
    .controller('UserOverviewCtrl', function ($scope, FestivalUsers, User) {

        $scope.userArray = [];

        for (var i = 0; i < FestivalUsers.length; i++) {
            $scope.userArray[i] = new User(FestivalUsers[i]);
        }
    });
