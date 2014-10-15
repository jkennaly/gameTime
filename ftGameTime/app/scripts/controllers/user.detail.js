/**
 * Created by jbk on 10/5/14.
 */

/**
 * @ngdoc function
 * @name ftGameTimeApp.controller:UserDetailCtrl
 * @description
 * # UserDetailCtrl
 * Controller of the ftGameTimeApp
 */
angular.module('ftGameTimeApp')
    .controller('UserDetailCtrl', function ($scope, $routeParams, Objectify, User, FestivalDate) {
        var festStatus;
        var userFestivalData = Objectify.result("userFestivalData");
        var selfData = Objectify.result("selfData");
        $scope.currentFestivalData = FestivalDate;
        $scope.userImage = userFestivalData[$routeParams.userID].img["userImage-" + $routeParams.userID];
        $scope.user = {};
        $scope.user.current = new User($routeParams.userID);

        //display festival status
        if ($scope.user.current.purchased) festStatus = " is signed up for "; else festStatus = " is not signed up for ";
        $scope.status = ($scope.user.current.username + festStatus + FestivalDate.sitename);
        $scope.self = $scope.user.current.id == selfData.id;
    });
