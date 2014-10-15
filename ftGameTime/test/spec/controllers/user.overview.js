'use strict';

describe('Controller: UserOverviewCtrl', function () {

    // load the controller's module
    beforeEach(module('ftGameTimeApp'));

    var UserOverviewCtrl,
        scope;

    // Initialize the controller and a mock scope
    beforeEach(inject(function ($controller, $rootScope, FestivalUsers, User) {
        scope = $rootScope.$new();
        UserOverviewCtrl = $controller('UserOverviewCtrl', {
            $scope: scope
        });
    }));

    it('should attach a list of awesomeThings to the scope', function () {
        expect(scope.userArray.length).toBe(3);
    });
});
