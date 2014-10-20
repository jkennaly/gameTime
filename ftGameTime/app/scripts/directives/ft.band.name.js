/**
 * Created by jbk on 10/7/14.
 */

angular.module('ftGameTimeApp')
    .directive('ftBandName', function () {
//        console.log("Directive was called again");
        return {
            restrict: 'E',
            scope: {
                bandId: '='
            },
//        template: '<div > Test</div>'
            templateUrl: "views/templates/ft.band.name.tmpl.html",
            controller: function ($scope, Band) {
                if ($scope.bandId) {
                    var band = new Band($scope.bandId);
                    $scope.band = band;

                } else $scope.band = {};

            }

        };
    });