/**
 * Created by jbk on 10/7/14.
 */

angular.module('ftGameTimeApp')
    .directive('currentShows', function () {
//        console.log("Directive was called again");
        return {
            restrict: 'E',
            scope: {
                band: '='
            },
//        template: '<div > Test</div>'
            templateUrl: "views/templates/current.show.reports.tmpl.html",
            controller: function ($scope, BandCardsCurrent) {
                var cards = BandCardsCurrent.cards($scope.band.current.id);


                //               console.log(cards);
                $scope.band.current.currentCards = cards;
            }

        };
    });