'use strict';

/**
 * @ngdoc overview
 * @name ftGameTimeApp
 * @description
 * # ftGameTimeApp
 *
 * Main module of the application.
 */
angular
    .module('ftGameTimeApp', [
        'ngAnimate',
        'ngCookies',
        'ngResource',
        'ngRoute',
        'ngSanitize',
        'ngTouch',
        'ionic',
        'ionic.rating'
    ])
    .config(function ($routeProvider) {
        $routeProvider
            .when('/', {
                templateUrl: 'views/main.html',
                controller: 'MainCtrl'
            })
            .when('/about', {
                templateUrl: 'views/about.html',
                controller: 'AboutCtrl'
            })
            .when('/band/detail/:bandID', {
                templateUrl: 'views/band.detail.frag.html',
                controller: 'BandDetailCtrl'
            })
            .when('/band/overview', {
                templateUrl: 'views/band.overview.frag.html',
                controller: 'BandOverviewCtrl'
            })
            .when('/festival/home', {
                templateUrl: 'views/festival.home.frag.html',
                controller: 'FestivalHomeCtrl'
            })
            .when('/festival/select', {
                templateUrl: 'views/festival.select.frag.html',
                controller: 'FestivalSelectCtrl'
            })
            .when('/login/create', {
                templateUrl: 'views/login.create.frag.html',
                controller: 'LoginCreateCtrl'
            })
            .when('/login/failed/network', {
                templateUrl: 'views/login.failed.network.frag.html',
                controller: 'LoginFailedNetworkCtrl'
            })
            .when('/login/failed/password', {
                templateUrl: 'views/login.failed.password.frag.html',
                controller: 'LoginFailedPasswordCtrl'
            })
            .when('/login/failed/username', {
                templateUrl: 'views/login.failed.username.frag.html',
                controller: 'LoginFailedUsernameCtrl'
            })
            .when('/login/forgot', {
                templateUrl: 'views/login.forgot.password.frag.html',
                controller: 'LoginForgotCtrl'
            })
            .when('/login', {
                templateUrl: 'views/login.frag.html',
                controller: 'LoginCtrl'
            })
            .when('/user/detail/:userID', {
                templateUrl: 'views/user.detail.frag.html',
                controller: 'UserDetailCtrl'
            })
            .when('/user/overview', {
                templateUrl: 'views/user.overview.frag.html',
                controller: 'UserOverviewCtrl'
            })
            .otherwise({
                redirectTo: '/'
            });
    });
