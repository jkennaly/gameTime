/**
 * Created by jbk on 10/5/14.
 */
'use strict';
/**
 * @ngdoc function
 * @name ftGameTimeApp.controller:LoginFailedNetworkCtrl
 * @description
 * # ScheduleCtrl
 * Controller of the ftGameTimeApp
 */
angular.module('ftGameTimeApp')
    .controller('ScheduleCtrl', function ($scope, FestivalDays, Day) {
        $scope.day = new Day(FestivalDays[0]);
    });
