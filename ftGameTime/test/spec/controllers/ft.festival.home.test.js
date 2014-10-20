'use strict';

describe('Controller: FestivalHomeCtrl', function () {

    // load the controller's module
    beforeEach(module('ftGameTimeApp'));

    var FestivalHomeCtrl, testObj,
        scope;

    // Initialize the controller and a mock scope
    beforeEach(inject(function ($controller, $rootScope) {
        scope = $rootScope.$new();
        testObj = {fasg: 5432, jisd: "gfdsghdsht"};
        FestivalHomeCtrl = $controller('FestivalHomeCtrl', {
            $scope: scope,
            FestivalFestival: testObj
        });
    }));

    it('should attach the current FestivalFestival object to the scope', function () {
        expect(scope.curFest).toEqual(testObj);
    });
});
