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
            templateUrl: "views/templates/set.display.tmpl.html"
        };
    });