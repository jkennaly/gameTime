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
        'ionic.rating',
        'angularMoment',
        'ftConfig'
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

            .state('ft', {
                url: '/ft',
                abstract: true,
                template: '<ion-nav-view></ion-nav-view>'
            })

            .state('ft.login', {
                url: '/login',
                abstract: true,
                template: '<ion-nav-view></ion-nav-view>'
            })
            .state('ft.login.create', {
                url: '/create',

                templateUrl: 'views/login.create.frag.html',
                controller: 'LoginCreateCtrl'
            })
            .state('ft.login.forgot', {
                url: '/forgot',
                templateUrl: 'views/login.forgot.password.frag.html',
                controller: 'LoginForgotCtrl'

            })
            .state('ft.login.login', {
                url: '/login',

                templateUrl: 'views/login.frag.html',
                controller: 'LoginCtrl'
            })
            .state('ft.login.failed', {
                url: '/failed',
                abstract: true,
                template: '<ion-nav-view></ion-nav-view>'
            })
            .state('ft.login.failed.network', {
                url: '/network',
                templateUrl: 'views/login.failed.network.frag.html',
                controller: 'LoginFailedNetworkCtrl'

            })
            .state('ft.login.failed.password', {
                url: '/password',
                templateUrl: 'views/login.failed.password.frag.html',
                controller: 'LoginFailedPasswordCtrl'

            })
            .state('ft.login.failed.username', {
                url: '/username',
                templateUrl: 'views/login.failed.username.frag.html',
                controller: 'LoginFailedUsernameCtrl'
            })
            .state('ft.gt', {
                url: '/gametime',
                abstract: true,
                templateUrl: 'views/tabs/ft.gt.tmpl.html'
            })
            .state('ft.gt.band', {
                url: '/band',
                abstract: true,
                views: {
                    'ft-gt-band': {
                        template: '<ion-nav-view name="ft-gt-band"></ion-nav-view>'
                    }
                }
            })
            .state('ft.gt.band.overview', {
                url: '/overview',
                views: {
                    'ft-gt-band': {
                        templateUrl: 'views/band.overview.frag.html',
                        controller: 'BandOverviewCtrl'
                    }
                }
            })
            .state('ft.gt.band.detail', {
                url: '/:bandID',
                views: {
                    'ft-gt-band': {
                        templateUrl: 'views/band.detail.frag.html',
                        controller: 'BandDetailCtrl'
                    }
                }
            })
            .state('ft.gt.festival', {
                url: '/festival',
                abstract: true,
                views: {
                    'ft-gt-festival': {
                        template: '<ion-nav-view name="ft-gt-festival"></ion-nav-view>'
                    }
                }

            })
            .state('ft.gt.festival.home', {
                url: '/home',
                views: {
                    'ft-gt-festival': {
                        templateUrl: 'views/festival.home.frag.html',
                        controller: 'FestivalHomeCtrl'
                    }
                }
            })
            .state('ft.gt.festival.select', {
                url: '/select',
                views: {
                    'ft-gt-festival': {
                        templateUrl: 'views/festival.select.frag.html',
                        controller: 'FestivalSelectCtrl'
                    }
                }
            })
            .state('ft.gt.user', {
                url: '/user',
                abstract: true,
                views: {
                    'ft-gt-user': {
                        template: '<ion-nav-view name="ft-gt-user"></ion-nav-view>'
                    }
                }
            })
            .state('ft.gt.user.overview', {
                url: '/overview',
                views: {
                    'ft-gt-user': {
                        templateUrl: 'views/user.overview.frag.html',
                        controller: 'UserOverviewCtrl'
                    }
                }
            })
            .state('ft.gt.user.detail', {
                url: '/:userID',
                views: {
                    'ft-gt-user': {
                        templateUrl: 'views/user.detail.frag.html',
                        controller: 'UserDetailCtrl'
                    }
                }
            })
            .state('ft.gt.schedule', {
                url: '/schedule',
                views: {
                    'ft-gt-schedule': {
                        templateUrl: 'views/schedule.frag.html',
                        controller: 'ScheduleCtrl'
                    }
                }
            });
        // if none of the above states are matched, use this as the fallback
        $urlRouterProvider.otherwise(function () {
            var user = localStorage.getItem('uname');
            var fest = localStorage.getItem('currentFestivalData');
            var retVal;
            if (fest) retVal = '/ft/gametime/band/overview';
            else if (user) retVal = '/ft/festival/select';
            else retVal = '/ft/login/login';
            return retVal;
        });
    });
