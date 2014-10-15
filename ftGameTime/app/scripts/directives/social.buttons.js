/**
 * Created by jbk on 10/7/14.
 */

angular.module('ftGameTimeApp')
    .directive('socialButtons', function () {

        console.log("Directive was called");
        return {
            restrict: 'E',
            scope: {
                user: '=ngModel'
            },
//        template: '<div class="button-bar"><a class="button ion-paper-airplane">Test {{user.username}} {{user.other()}}</a></div>'
            templateUrl: "views/templates/social.buttons.tmpl.html"

        };
    });