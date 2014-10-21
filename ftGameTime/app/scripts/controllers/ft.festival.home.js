/**
 * Created by jbk on 10/5/14.
 */
'use strict';
/**
 * @ngdoc function
 * @name ftGameTimeApp.controller:FestivalHomeCtrl
 * @description
 * # FestivalHomeCtrl
 * Controller of the ftGameTimeApp
 */
angular.module('ftGameTimeApp')
    .controller('FestivalHomeCtrl', function ($scope, FestivalFestival) {
        $scope.curFest = FestivalFestival;
    });
