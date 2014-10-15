/**
 * Created by jbk on 10/5/14.
 */

angular.module('ftGameTime.services', [])
    .factory('login', ['$http', function ($http) {

        var doRequest = function (username, path) {
            return $http({
                method: 'JSONP',
                url: 'https://api.github.com/users/' + username + '/' + path + '?callback=JSON_CALLBACK'
            });
        }
        return {
            events: function (username) {
                return doRequest(username, 'events');
            }
        };
    }]);
