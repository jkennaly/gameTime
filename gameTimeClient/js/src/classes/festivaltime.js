/**
 * Created by jbk on 10/3/14.
 */

// create the root namespace and making sure we're not overwriting it
var FESTIVALTIME = FESTIVALTIME || {};

// create a general purpose namespace method
// this will allow us to create namespace a bit easier
FESTIVALTIME.createNS = function (namespace) {
    var nsparts = namespace.split(".");
    var parent = FESTIVALTIME;

    // we want to be able to include or exclude the root namespace 
    // So we strip it if it's in the namespace
    if (nsparts[0] === "FESTIVALTIME") {
        nsparts = nsparts.slice(1);
    }

    // loop through the parts and create 
    // a nested namespace if necessary
    for (var i = 0; i < nsparts.length; i++) {
        var partname = nsparts[i];
        // check if the current parent already has 
        // the namespace declared, if not create it
        if (typeof parent[partname] === "undefined") {
            parent[partname] = {};
        }
        // get a reference to the deepest element 
        // in the hierarchy so far
        parent = parent[partname];
    }
    // the parent is now completely constructed 
    // with empty namespaces and can be used.
    return parent;
};

// Create the namespace for users
/** @namespace FESTIVALTIME.MODEL.USERS */
/** @namespace FESTIVALTIME.MODEL */
FESTIVALTIME.createNS("FESTIVALTIME.MODEL.USERS");

FESTIVALTIME.MODEL.USERS.user = function (userid) {
    // private variables
    var username, follows, blocks, imageSRC, recentFests, purchased, atFav;
    var atWorst, gtFav, pgFav, id;
    // private methods
    // creating getWidth and getHeight
    // to prevent access by reference to dimension
    var setFields = function (userid) {
        var userDataTemp;
        var userDataAll = FESTIVALTIME.LOGIC.GAMETIME.objectify("userFestivalData");
        userDataTemp = userDataAll ? userDataAll[userid] : false;

        /** @namespace userDataTemp.generalData */
        this.username = userDataTemp.generalData.username;
        this.follows = userDataTemp.generalData.follows;
        this.blocks = userDataTemp.generalData.blocks;
        this.imageSRC = userDataTemp.img["userImage-" + userid];
        this.recentFests = userDataTemp.recentFests;
        this.purchased = userDataTemp.purchased;
        this.atFav = userDataTemp.atFav;
        this.atWorst = userDataTemp.atWorst;
        this.gtFav = userDataTemp.gtFav;
        this.pgFav = userDataTemp.pgFav;
        this.id = userid;
    };
    setFields(this.id);

};

// Create the namespace for the logic
/** @namespace FESTIVALTIME.LOGIC.GAMETIME */
/** @namespace FESTIVALTIME.LOGIC */
FESTIVALTIME.createNS("FESTIVALTIME.LOGIC.GAMETIME");


FESTIVALTIME.LOGIC.GAMETIME.createAndAlertUser = function () {
    var model = FESTIVALTIME.MODEL.USERS;
    var p = new model.user(2);
    alert(p.getWidth() + " " + p.getHeight());
};

FESTIVALTIME.LOGIC.GAMETIME.objectify = function (key) {
    return JSON.parse(localStorage.getItem(key));
};

FESTIVALTIME.LOGIC.GAMETIME.sortUsers = function (sortOrder) {
    sortType = sortType || "default";
    var userData = objectify("userFestivalData");
    var returnArray = [];

//    alert("sorting users by: " + sortType);

    if (sortType == "default") {
        var selfData = objectify("selfData");
        //Create arrays to use:
        var self = [], followedCheckin = [], followedPurchased = [], followed = [];
        var followerCheckin = [], followerPurchased = [], follower = [];
        var unknownCheckin = [], unknownPurchased = [], unknown = [];

        $.each(userData, function (i, val) {
            //self first
            if (i == selfData.id) {
                self.push(i);
                return;
            }
            //followed users who have a check in at  the current festival
            //followed users who have purchased the festival
            if (val.purchased === true && $.inArray(i, selfData.follows)) {
                followedPurchased.push(i);
                return;
            }
            //followed users who have not purchased the festival
            if ($.inArray(i, selfData.follows)) {
                followed.push(i);
                return;
            }

            //unfollowed users who follow self who have a check in at the current festival
            //unfollowed users who follow self who have purchased the current festival
            if (val.purchased === true && $.inArray(selfData.id, val.follows)) {
                followerPurchased.push(i);
                return;
            }
            //unfollowed users who follow self who have not purchased the current festival
            if ($.inArray(selfData.id, val.follows)) {
                follower.push(i);
                return;
            }
            //unfollowed users who do not follow self who have a check in at the current festival
            //unfollowed users who do not follow self who have purchased the current festival
            if (val.purchased === true) {
                unknownPurchased.push(i);
                return;
            }
            //unfollowed users who do not follow self who have not purchased the current festival
            unknown.push(i);

        });
        if (self) returnArray = returnArray.concat(self);
        if (followedCheckin) returnArray = returnArray.concat(followedCheckin);
        if (followedPurchased) returnArray = returnArray.concat(followedPurchased);
        if (followed) returnArray = returnArray.concat(followed);
        if (followerCheckin) returnArray = returnArray.concat(followerCheckin);
        if (followerPurchased) returnArray = returnArray.concat(followerPurchased);
        if (follower) returnArray = returnArray.concat(follower);
        if (unknownCheckin) returnArray = returnArray.concat(unknownCheckin);
        if (unknownPurchased) returnArray = returnArray.concat(unknownPurchased);
        if (unknown) returnArray = returnArray.concat(unknown);
    }
    localStorage.setItem("userSortOrder", JSON.stringify(returnArray));

};