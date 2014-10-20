/**
 * Created by jbk on 10/7/14.
 */

angular.module('ftGameTimeApp')
    .directive('ftPlaceName', function () {
//        console.log("Directive was called again");
        return {
            restrict: 'E',
            scope: {
                placeId: '='
            },
//        template: '<div > Test</div>'
            templateUrl: "views/templates/ft.place.name.tmpl.html",
            controller: function ($scope, Place) {
                if ($scope.placeId) {
                    $scope.place = new Place($scope.placeId);
                } else $scope.place = {};

            }

        };
    });