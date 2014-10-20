/**
 * Created by jbk on 10/7/14.
 */

angular.module('ftGameTimeApp')
    .directive('ftDayName', function () {
//        console.log("Directive was called again");
        return {
            restrict: 'E',
            scope: {
                dayId: '='
            },
//        template: '<div > Test</div>'
            templateUrl: "views/templates/ft.day.name.tmpl.html",
            controller: function ($scope, Day) {
                if ($scope.dayId) {
                    var day = new Day($scope.dayId);
                    $scope.day = day;
                } else $scope.day = {};

            }

        };
    });