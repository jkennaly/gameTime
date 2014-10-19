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

    .run(function ($ionicPlatform) {
        $ionicPlatform.ready(function () {
            // Hide the accessory bar by default (remove this to show the accessory bar above the keyboard
            // for form inputs)
            if (window.cordova && window.cordova.plugins.Keyboard) {
                cordova.plugins.Keyboard.hideKeyboardAccessoryBar(true);
            }
            if (window.StatusBar) {
                // org.apache.cordova.statusbar required
                StatusBar.styleDefault();
            }
        });
    })

    .config(function ($stateProvider, $urlRouterProvider) {

        // Ionic uses AngularUI Router which uses the concept of states
        // Learn more here: https://github.com/angular-ui/ui-router
        // Set up the various states which the app can be in.
        // Each state's controller can be found in controllers.js
        $stateProvider
            // setup an abstract state for the tabs directive
            .state('tab', {
                url: "/tab",
                abstract: true,
                templateUrl: "views/tabs/tabs.html"
            })
            .state('tab.band-overview', {
                url: '/band/overview',
                views: {
                    'tab-band-overview': {
                        templateUrl: 'views/band.overview.frag.html',
                        controller: 'BandOverviewCtrl'
                    }
                }
            })
            .state('tab.band-detail', {
                url: '/band/detail/:bandID',
                views: {
                    'tab-band-overview': {
                        templateUrl: 'views/band.detail.frag.html',
                        controller: 'BandDetailCtrl'
                    }
                }
            })
            .state('tab.festival-home', {
                url: '/festival/home',
                views: {
                    'tab-festival-home': {
                        templateUrl: 'views/festival.home.frag.html',
                        controller: 'FestivalHomeCtrl'
                    }
                }
            })
            .state('tab.festival-select', {
                url: '/festival/select',
                views: {
                    'tab-festival-select': {
                        templateUrl: 'views/festival.select.frag.html',
                        controller: 'FestivalSelectCtrl'
                    }
                }
            })
            .state('tab.login-create', {
                url: '/login/create',
                views: {
                    'tab-login-create': {
                        templateUrl: 'views/login.create.frag.html',
                        controller: 'LoginCreateCtrl'
                    }
                }
            })
            .state('tab.login-failed-network', {
                url: '/login/failed/network',
                views: {
                    'tab-login-failed-network': {
                        templateUrl: 'views/login.failed.network.frag.html',
                        controller: 'LoginFailedNetworkCtrl'
                    }
                }
            })
            .state('tab.login-failed-password', {
                url: '/login/failed/network',
                views: {
                    'tab-login-failed-network': {
                        templateUrl: 'views/login.failed.password.frag.html',
                        controller: 'LoginFailedPasswordCtrl'
                    }
                }
            })
            .state('tab.login-failed-username', {
                url: '/login/failed/username',
                views: {
                    'tab-login-failed-username': {
                        templateUrl: 'views/login.failed.username.frag.html',
                        controller: 'LoginFailedUsernameCtrl'
                    }
                }
            })
            .state('tab.login-forgot', {
                url: '/login/failed/username',
                views: {
                    'tab-login-forgot': {
                        templateUrl: 'views/login.forgot.password.frag.html',
                        controller: 'LoginForgotCtrl'
                    }
                }
            })
            .state('tab.login', {
                url: '/login',
                views: {
                    'tab-login': {
                        templateUrl: 'views/login.frag.html',
                        controller: 'LoginCtrl'
                    }
                }
            })
            .state('tab.user-overview', {
                url: '/user/overview',
                views: {
                    'tab-user-overview': {
                        templateUrl: 'views/user.overview.frag.html',
                        controller: 'UserOverviewCtrl'
                    }
                }
            })
            .state('tab.user-detail', {
                url: '/user/detail/:userID',
                views: {
                    'tab-user-overview': {
                        templateUrl: 'views/user.detail.frag.html',
                        controller: 'UserDetailCtrl'
                    }
                }
            });
        // if none of the above states are matched, use this as the fallback
        $urlRouterProvider.otherwise('/tab/festival/home');
    });
