/**
 * Created by jbk on 10/7/14.
 */

angular.module('ftGameTimeApp')
    .directive('ftSchedule', function () {
//        console.log("Directive was called again");
        return {
            restrict: 'E',
            scope: {
                dayId: '='
            },
//        template: '<div > Test</div>'
            templateUrl: "views/templates/ft.schedule.tmpl.html",
            controller: function ($scope, $filter, Day, Place, Set, FestivalFestival, FestivalPlaces, FestivalSets, FestivalDate) {
                var i;
                var now = new Date();
                var startOfDay = new Date(now.getFullYear(), now.getMonth(), now.getDate());
                var startTime = new Date((FestivalFestival.start_time) * 1000 + startOfDay.getTime());

                $scope.festival = {};
                $scope.festival.places = [];
                $scope.festival.sets = [];
                $scope.time = {};
                $scope.time.hours = [];
                if ($scope.dayId) {
                    var day = new Day($scope.dayId);
                    $scope.day = day;
                } else $scope.day = {};
                for (i = 0; i < FestivalFestival.length; i += 3600) {
                    var displayTime = new Date((i + FestivalFestival.start_time) * 1000 + startOfDay.getTime());
                    $scope.time.hours.push(displayTime);
                }
                for (i = 0; i < FestivalPlaces.length; i++) {
                    $scope.festival.places.push(new Place(FestivalPlaces[i]));
//                    console.log("stage: " + $scope.festival.places[i].name + " type: " + $scope.festival.places[i].type);
                }
                console.log("festival base date: " + FestivalDate.baseDate);

                var dayStart = new Date(FestivalFestival.start_time * 1000 + day.offset * 24 * 3600 * 1000 + FestivalDate.baseDate);
                console.log("festival day start: " + dayStart);

                var prevEnd = dayStart;
                var prevDay = 0;
                var prevStage = 0;
//                console.log("Sets: ", FestivalSets);
                var setsT = $filter('orderBy')(FestivalSets, ['day', 'stage', 'startTime']);
                var sets = $filter('filter')(setsT, {day: day.id});
//                console.log("Sorted Sets: ", sets);

                for (i = 0; i < sets.length; i++) {

                    var set = new Set(sets[i].id);
                    set.prevEnd = prevDay == set.day && prevStage == set.stage ? prevEnd : dayStart;
                    set.space = set.startTime - prevEnd;
                    set.millis = set.endTime - set.startTime;
                    $scope.festival.sets.push(set);
                    prevEnd = set.endTime;
                    prevDay = set.day;
                    prevStage = set.stage;
//                    console.log("Set: ", set);
                }
            }

        };
    });