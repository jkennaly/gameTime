/**
 * Created by jbk on 10/9/14.
 */

angular.module('ftGameTimeApp')
    .factory('Band', function (Objectify) {


        var bandFestivalData = Objectify.result("bandFestivalData");
        // instantiate our initial object
        var Band = function (id) {
            this.name = bandFestivalData[id].name;
            this.id = id;
            this.ratingsData = bandFestivalData[id].ratingsData;
            this.genre = bandFestivalData[id].genre;
            this.parentGenre = bandFestivalData[id].parentGenre;
            this.cardData = bandFestivalData[id].cardData;
        }
        return Band;
    });