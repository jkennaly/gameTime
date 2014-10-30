/**
 * Created by jbk on 10/9/14.
 */

angular.module('ftGameTimeApp')
    .factory('Band', function (Objectify, FestivalSets, Set, FestivalBands) {


        var bandFestivalData = Objectify.result("bandFestivalData");
        // instantiate our initial object
        var isInt = function is_int(value) {
            for (var i = 0; i < value.length; i++) {
                if ((value.charAt(i) < '0') || (value.charAt(i) > '9')) return false
            }
            return true;
        };
        var Band = function (bandId) {
            //Id may come in as a string (corresponding to name)
            //Or as an int (correpsonding to id)
            //If it cannot be traced to a band in
            //bandFestivalData, the value is returned as name
            //(If not an integer) or as id (if an integer)
            var id = 0;
            if (isInt(bandId)) {
                id = bandId;
            } else {
//                console.log(bandFestivalData);
                for (var i = 0; i < FestivalBands.length; i++) {
                    //                   console.log(bandFestivalData[FestivalBands[i]].name);
                    if (bandFestivalData[FestivalBands[i]].name == bandId) {
                        id = bandFestivalData[FestivalBands[i]].id;
                        break;
                    }
//                    console.log(bandId);
//                    console.log(id);
                }

            }
            if (id) {
                this.name = bandFestivalData[id].name;
                this.id = id;
                this.priority = bandFestivalData[id].priority;
                this.imageSrc = bandFestivalData[id].imageSrc;
                this.ratingsData = bandFestivalData[id].ratingsData;
                this.genre = bandFestivalData[id].genre;
                this.parentGenre = bandFestivalData[id].parentGenre;
                this.cardData = bandFestivalData[id].cardData;
            } else {
                this.id = 0;
                this.name = bandId;
            }

        };

        /*   Band.prototype.getSets = function () {
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
         }*/

        return Band;
    });