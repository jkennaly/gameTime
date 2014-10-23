/**
 * Created by jbk on 10/7/14.
 */

angular.module('ftGameTimeApp')
    .directive('setDisplay', function () {
//        console.log("Directive was called again");
        return {
            restrict: 'E',
            scope: {
                set: '='
            },
//        template: '<div > Test</div>'
            templateUrl: "views/templates/set.display.tmpl.html",
            controller: function ($scope, FestivalDate, moment) {

                var start = moment($scope.set.startTime);
                $scope.set.startTimeH = start.tz(FestivalDate.venueData.timeZone).format('h:mm');
                var end = moment($scope.set.endTime);
                $scope.set.endTimeH = end.tz(FestivalDate.venueData.timeZone).format('h:mm');

            }
        };
    });