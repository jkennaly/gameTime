/**
 * Created by jbk on 10/6/14.
 */

angular.module('ftGameTimeApp')
    .factory('Objectify', [function ObjectifyFactory() {

        return {
            result: function (key) {
                return JSON.parse(localStorage.getItem(key));
            }
        }

    }]);
