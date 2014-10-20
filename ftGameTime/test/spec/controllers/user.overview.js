'use strict';

describe('Controller: UserOverviewCtrl', function () {

    // load the controller's module
    beforeEach(module('ftGameTimeApp'));

    var UserOverviewCtrl, festUsers, User,
        scope;

    // Initialize the controller and a mock scope
    beforeEach(inject(function ($controller, $rootScope) {
        scope = $rootScope.$new();
        festUsers = [32, 56];
        User = function (id) {
            this.id = id;
        };

        UserOverviewCtrl = $controller('UserOverviewCtrl', {
            $scope: scope,
            FestivalUsers: festUsers,
            User: User
        });
    }));

    it('should put an array of user objects on the scope, with id = to correponding element in FestivalUsers[]', function () {
        expect(scope.userArray.length).toBe(festUsers.length);
        expect(scope.userArray[0].id).toBe(festUsers[0]);
        expect(scope.userArray[1].id).toBe(festUsers[1]);
        expect(scope.userArray[1].id).toBe(56);
        expect(scope.userArray[0].id).toBe(32);
    });
});
