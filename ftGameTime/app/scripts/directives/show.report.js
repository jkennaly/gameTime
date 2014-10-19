/**
 * Created by jbk on 10/7/14.
 */

angular.module('ftGameTimeApp')
    .directive('showReport', function () {
//        console.log("Directive was called again");
        return {
            restrict: 'E',
            scope: {
                card: '='
            },
//        template: '<div > Test</div>'
            templateUrl: "views/templates/show.report.tmpl.html",
            controller: function ($scope, User) {
                $scope.reportUserImage = function (reportAuthor) {
                    var user = new User(reportAuthor);
                    return user.imageSrc;
                };
                $scope.reportUserName = function (reportAuthor) {
                    var user = new User(reportAuthor);
                    return user.username;
                };
            }

        };
    });