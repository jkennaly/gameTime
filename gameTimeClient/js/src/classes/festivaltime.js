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
        username = userDataTemp.generalData.username;
        follows = userDataTemp.generalData.follows;
        blocks = userDataTemp.generalData.blocks;
        imageSRC = userDataTemp.img["userImage-" + userid];
        recentFests = userDataTemp.recentFests;
        purchased = userDataTemp.purchased;
        atFav = userDataTemp.atFav;
        atWorst = userDataTemp.atWorst;
        gtFav = userDataTemp.gtFav;
        pgFav = userDataTemp.pgFav;
        id = userid;
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