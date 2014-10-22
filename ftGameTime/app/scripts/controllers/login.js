/**
 * Created by jbk on 10/5/14.
 */

/**
 * @ngdoc function
 * @name ftGameTimeApp.controller:LoginCtrl
 * @description
 * # LoginCtrl
 * Controller of the ftGameTimeApp
 */
'use strict';
angular.module('ftGameTimeApp')
    .controller('LoginCtrl', function ($scope, AppServer, $state) {

        localStorage.clear();

        $scope.loginForm = {};

        // process the form
        $scope.submitLogin = function () {
            var data = {
                reqType: 'challenge_req',
                uname: $scope.loginForm.username
            };
//      alert('data: ' + data.uname);
            var reqChallenge = AppServer.request(data).response;
            reqChallenge.success(function (data) {
                // receive salt and challenge, or that the username does not exist
                if (!data.uname) {
                    // if DNE, suggest creating an account or allow retry
                    $state.go('^.failed.username');
                    return false;
                }
                // if exists, concatenate submitted password with salt
                var esc = function (stringToEscape) {
                    return stringToEscape
                        .replace("\\", "\\\\")
                        .replace("\'", "\\\'")
                        .replace("\"", "\\\"")
                        .replace("\n", "\\\n")
                        .replace("\r", "\\\r")
                        .replace("\x00", "\\\x00")
                        .replace("\x1a", "\\\x1a");
                };
                var escapedPW = esc($scope.loginForm.password);

                var saltedPW = escapedPW + data.salt;
                // perform sha256 on salted password
                var hashedPW = sjcl.codec.hex.fromBits(sjcl.hash.sha256.hash(saltedPW));

                // concatenate hashed password with challenge
                var challPW = hashedPW + data.challenge;
                // hash challenged password
                var hashChallPW = sjcl.codec.hex.fromBits(sjcl.hash.sha256.hash(challPW));
                //        alert(hashChallPW);
                // send response
                var data2 = {
                    reqType: 'challenge_sub',
                    uname: data.uname,
                    response: hashChallPW
                };
                var reqResponse = AppServer.request(data2).response;
                reqResponse.success(function (data) {
                    // receive either an auth token, or that the password is incorrect
//                    console.log("So far so good");
                    if (data.pwValid) {
                        localStorage.setItem('mobile_auth_key', data.auth_key);
                        localStorage.setItem('uname', data.uname);
                        //                       alert('about to call festival_select');
                        $state.go('ft.gt.festival.select');
                        $window.location.reload();

                        return true;
                    } else {
                        // if password is incorrect, suggest forgot password link or allow retry
                        $state.go('^.failed.password');
                        return true;
                    }
                }).error(function () {
                    $state.go('^.failed.network');
                    return false;
                });

            }).error(function () {
                $state.go('^.failed.network');
                return false;
            });

        };

    });
