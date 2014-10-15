/**
 * Created by jbk on 10/8/14.
 */

angular.module('ftGameTimeApp')
    .factory('User', function (Objectify, FestivalSelf) {


        var userFestivalData = Objectify.result("userFestivalData");
        // instantiate our initial object
        var User = function (id) {
            this.username = userFestivalData[id].generalData.username;
            this.id = id;
            this.imageSrc = userFestivalData[id].img["userImage-" + id];
            this.recentFests = userFestivalData[id].recentFests;
            this.purchased = userFestivalData[id].purchased;
            this.atFav = userFestivalData[id].atFav;
            this.atWorst = userFestivalData[id].atWorst;
            this.gtFav = userFestivalData[id].gtFav;
            this.pgFav = userFestivalData[id].pgFav;
            this.other = function () {
                return FestivalSelf.id != id;
            };
            this.followed = function () {
                return this.other && (FestivalSelf.follows.indexOf(parseInt(this.id)) > -1);
            };
            this.blocked = function () {
                return this.other && (FestivalSelf.blocks.indexOf(parseInt(this.id)) > -1);
            };


        };
        User.prototype.followToggle = function () {
            if (this.followed()) {
                var index = FestivalSelf.follows.indexOf(parseInt(this.id));
                FestivalSelf.follows.splice(index, 1);
            } else FestivalSelf.follows.push(parseInt(this.id));
            localStorage.setItem("selfData", JSON.stringify(FestivalSelf));
        }

        User.prototype.blockToggle = function () {
            if (this.blocked()) {
                var index = FestivalSelf.blocks.indexOf(parseInt(this.id));
                FestivalSelf.blocks.splice(index, 1);
            } else FestivalSelf.blocks.push(parseInt(this.id));
            localStorage.setItem("selfData", JSON.stringify(FestivalSelf));
        }

        // define the getProfile method which will fetch data
        // from GH API and *returns* a promise
        /*
         SimpleGithubUser.prototype.getProfile = function() {

         // Generally, javascript callbacks, like here the $http.get callback,
         // change the value of the "this" variable inside it
         // so we need to keep a reference to the current instance "this" :
         var self = this;

         return $http.get(apiUrl + 'users/' + this.username).then(function(response) {

         // when we get the results we store the data in user.profile
         self.profile = response.data

         // promises success should always return something in order to allow chaining
         return response;

         });
         };
         */
        return User;
    });