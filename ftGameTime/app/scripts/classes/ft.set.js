/**
 * Created by jbk on 10/9/14.
 */

angular.module('ftGameTimeApp')
    .factory('Set', function (FestivalSets) {


        // instantiate our initial object
        var Set = function (id) {

            var targetSet = {};
            for (var i = 0; i < FestivalSets.length; i++) {
                /* console.log(FestivalSets[i]);
                 console.log(id);*/
                if (FestivalSets[i].id == id) {
                    targetSet = FestivalSets[i];
                    break;
                }
            }

            this.id = id;
            this.startTime = new Date(targetSet.startTime);
            this.endTime = new Date(targetSet.endTime);
            this.band = targetSet.band;
            this.day = targetSet.day;
            this.stage = targetSet.stage;

        };

        return Set;
    });