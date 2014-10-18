/**
 * Created by jbk on 10/9/14.
 */

angular.module('ftGameTimeApp')
    .factory('Band', function (Objectify, FestivalSets, Set) {


        var bandFestivalData = Objectify.result("bandFestivalData");
        // instantiate our initial object
        var Band = function (id) {
            this.name = bandFestivalData[id].name;
            this.id = id;
            this.priority = bandFestivalData[id].priority;
            this.imageSrc = bandFestivalData[id].imageSrc;
            this.ratingsData = bandFestivalData[id].ratingsData;
            this.genre = bandFestivalData[id].genre;
            this.parentGenre = bandFestivalData[id].parentGenre;
            this.cardData = bandFestivalData[id].cardData;
        };

        Band.prototype.getSets = function () {
            var sets = [];
            //           console.log(FestivalSets);
            for (var i = 0; i < FestivalSets.length; i++) {
                var set = new Set(FestivalSets[i]);
//                console.log(set);
                if (set.band == this.id) {
//                    console.log(set);
                    sets.push(set);
                }
            }
            return sets;
        }

        return Band;
    });