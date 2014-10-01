var v = [
    "selfData",
    "othersData",
    "purchasedFestivals",
    "unpurchasedFestivals",
    "userFestivalData",
    "currentFestivalData"
];

var curFest = false;

function getAuth() {
    var credentials = {
        mak: localStorage.getItem("mobile_auth_key"),
        uname: localStorage.getItem("uname")
    };

    if (!credentials.mak || !credentials.uname) return false;

    return credentials;
}

function sortIncoming(sortData) {
    if (sortData == "userFestivalData") sortUsers();
}

function sortUsers(sortType) {
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

}

function mysqlEscape(stringToEscape) {
    return stringToEscape
        .replace("\\", "\\\\")
        .replace("\'", "\\\'")
        .replace("\"", "\\\"")
        .replace("\n", "\\\n")
        .replace("\r", "\\\r")
        .replace("\x00", "\\\x00")
        .replace("\x1a", "\\\x1a");
}

/*
 function userLoggedIn(success, failure) {
 var credPresent = true;
 if (!getAuth()) credPresent = false;
 if (!credPresent) {
 callScreen(failure);
 return false;
 }
 var data = {
 reqType: "login_status",
 mobile_auth_key: credPresent.mak,
 uname: credPresent.uname
 };
 var logcheck = appServerReq(data);
 logcheck.done(function (d) {
 if (d.loginValid) {
 callScreen(success);
 return true;
 } else {
 callScreen(failure);
 return false;
 }
 }).fail(function (d) {
 callScreen(success);
 return true;
 });
 }
 */
function objectify(lsObject) {
    return JSON.parse(localStorage.getItem(lsObject));
}
/*
 function getFramework() {
 $.get('html.json', function (response) {
 localStorage.setItem("reloadRequired", "false");
 for (var i in response) {

 if (response.hasOwnProperty(i)) {  // this validates if prop belongs to obj
 localStorage.setItem(i, response[i]);
 }
 }
 }).always(function (d) {
 if (!curFest) userLoggedIn("festival_select", "login");
 else userLoggedIn("festival_home", "login");
 });
 }


 function drawFestHome() {
 curFest = objectify("currentFestivalData");
 //            alert(purFests);
 $("#footer-text").html(curFest.sitename);

 }
 */

$(document).on("pagecreate", "*", function () {
    var target = String($.mobile.path.get($(this).attr("value")));
//  alert(target);
    if (!target.localeCompare("login") && !getAuth()) {
        alert("Please log out and log in again.");
    }

});

$(document).on("pageinit", "#festival_home", function () {
    curFest = objectify("currentFestivalData");
//            alert(curFest.description);
    $("#footer-text").html(curFest.sitename);
});

$(document).on("pageinit", "#festival_select", function () {
//   alert("Festival select has been created");
    var purFests = JSON.parse(localStorage.getItem("purchasedFestivals"));
//            alert(purFests);
    if (purFests == null) {
        return false;
    }
    var $el = $("#select-purchased");
    $el.empty(); // remove old options
    purFests.forEach(function (element) {
//        alert(element.sitename);
        $el.append($("<option></option>")
            .attr("value", element.id).text(element.sitename));
    });
    // jQM refresh
    $el.selectmenu("refresh", true);
});


// Navigate to the next page on swipeleft
$(document).on("swipeleft", "#user_detail", function (event) {
    var currentDetail, nextDetail, prevDetil, sortOrder, arrayPos, arrayLength, nextPos;
    //figure out what id this is
    currentDetail = objectify("currentUserDetail");


    //figure out what id is next
    sortOrder = objectify("userSortOrder");
    arrayPos = $.inArray((currentDetail + ""), sortOrder);
    arrayLength = sortOrder.length;
    nextPos = arrayPos + 1;
    if (nextPos >= arrayLength) nextPos = 0;
    nextDetail = sortOrder[nextPos];


//        alert("swipe left-current detail/pos: " + currentDetail+"/"+arrayPos + "-order el 0: "+ sortOrder[0]+ " out of " + arrayLength + "-next detail: "+ nextDetail);

    localStorage.setItem("currentUserDetail", nextDetail);
    $(":mobile-pagecontainer").pagecontainer("change", "#user_detail", { allowSamePageTransition: true, transition: "slide" });
});
// The same for the navigating to the previous page
$(document).on("swiperight", "#user_detail", function (event) {
    var currentDetail, nextDetail, prevDetil, sortOrder, arrayPos, arrayLength, nextPos, prevPos;
    //figure out what id this is
    currentDetail = objectify("currentUserDetail");


    //figure out what id is next
    sortOrder = objectify("userSortOrder");
    arrayPos = $.inArray((currentDetail + ""), sortOrder);
    arrayLength = sortOrder.length;
    prevPos = arrayPos - 1;
    if (prevPos < 0) prevPos = arrayLength - 1;
    prevDetail = sortOrder[prevPos];


//        alert("swipe left-current detail/pos: " + currentDetail+"/"+arrayPos + "-order el 0: "+ sortOrder[0]+ " out of " + arrayLength + "-next detail: "+ nextDetail);

    localStorage.setItem("currentUserDetail", prevDetail);
    $(":mobile-pagecontainer").pagecontainer("change", "#user_detail", { allowSamePageTransition: true, transition: "slide", reverse: true });
});

$(document).on("pageshow", "#user_detail", function () {

    //Collect data/ assign variables
    curFest = objectify("currentFestivalData");
    var userFestivalData = objectify("userFestivalData");
    if (userFestivalData == null) {
        return false;
    }


    var query = localStorage.getItem("currentUserDetail");
//        alert(query);
    var user = userFestivalData[query];
    var imgId = '#profile-pic';

    //Add profilepic if available
    if (user.img.userImage == true) {
        var imageData = user.img["userImage-" + user.generalData.id];
        $(imgId).attr('src', imageData);

    } else {
        $(imgId).attr('src', '');
    }

    //display username
    $("#user-name").html(user.generalData.username);

    //display recent festivals
    var $el = $("#recent-fests");
    $el.empty(); // remove old options
    user.recentFests.forEach(function (element) {
//        alert(element.sitename);
        $el.append($("<li></li>")
            .text(element));
    });
    $el.listview("refresh", true);

    //display festival status
    var festStatus;
    if (user.purchased) festStatus = " is signed up for "; else festStatus = " is not signed up for ";
    $("#festival-status").html(user.generalData.username + festStatus + curFest.sitename);

    //display favorite bands
    $el = $("#all-time-favorites");
    $el.empty(); // remove old options
    //
    $el.append($('<li data-role="list-divider">Favorite bands</li>'));
    user.atFav.forEach(function (element) {
//        alert(element.sitename);
        $el.append($("<li></li>")
            .text(element));
    });

    //most disappointing show
    $el.append($('<li data-role="list-divider">Most disappointing show</li>'));

    if (user.atWorst) $el.append($("<li></li>").text(user.atWorst));

    //best so far
    $el.append($('<li data-role="list-divider">Best show so far</li>'));
    user.gtFav.forEach(function (element) {
//        alert(element.sitename);
        $el.append($("<li></li>")
            .text(element));
    });

    //excited to see
    $el.append($('<li data-role="list-divider">Excited to see</li>'));
    user.pgFav.forEach(function (element) {
//        alert(element.sitename);
        $el.append($("<li></li>")
            .text(element));
    });

    $el.listview("refresh", true);


});


function userListItem(user, $el) {
    var link = '#user_detail';
    var imgSrc = null;
    var username = user.generalData.username;
    var imageData = null;

//    alert( "test");
//    alert( user.generalData.id +": " + user.img.userImage);

//    alert("prevI: " + prevI + " / id: " + user.generalData.id);


    if (user.img.userImage == true) {
        imgSrc = 'user_overview_image_' + user.generalData.id;
        imageData = user.img["userImage-" + user.generalData.id];
    }
//    alert(imageData);

    var d = '<li class="ui-li-has-thumb ui-first-child"><a href="' + link +
        '" id="link-user-detail-' + user.generalData.id + '" class="detail_button" data-user-id="' + user.generalData.id + '" >' +
        '<img id="' + imgSrc + '"><h2>' + username + '</h2></a></li>';

    imgSrc = '#' + imgSrc;
    $el.append($(d));
//    console.log(imageData);
    $(imgSrc).attr('src', imageData);

    // jQM refresh
    $el.listview("refresh", true);

}

$(document).on("pageshow", "#user_overview", function () {
//   alert("Festival select has been created");
    var userFestivalData = objectify("userFestivalData");
    var userSortOrder = objectify("userSortOrder");
//    alert("test");

//    alert(userFestivalData["2"].generalData.username);

    if (userFestivalData == null) {
        return false;
    }
    var $el = $("#user-overview-list");
    $el.empty(); // remove old options
    $.each(userSortOrder, function (i, val) {
//        alert(val.generalData.username);
        userListItem(userFestivalData[val], $el);

    });

});

$(document).delegate("#login", "pagebeforecreate", function () {
    localStorage.clear();
});


function callScreen(screen) {

//    alert("calling: " + screen);
    var screenID = 'index.php#' + screen;
    /*
     $(":mobile-pagecontainer").pagecontainer('change', screenID, {
     transition: 'fade',
     changeHash: true,
     showLoadMsg: false
     });
     return false;
     */
    $.mobile.navigate(screenID, {transition: "slide", info: "info about the #bar hash"});

}


function appServerReq(data) {
    //Determine if request type is a 'login'-type request
    var loginRequests = [
        "login_status", "challenge_req", "challenge_sub"
    ];

    var cred = getAuth();

    //add check for request type against loginRequests


    if (cred != false) {
        data.mobile_auth_key = cred.mak;
        data.uname = cred.uname;
    } else if ($.inArray(data.reqType, loginRequests) == -1) {
        callScreen("login");
        return false;
    }


    //If there are credentials present, add credentials to submission
    //(unless request type is login_status, challenge_req, or challenge_sub

    //If there are not credentials present, redirect to login screen without executing request
    //(unless request type is login_status, challenge_req, or challenge_sub

//    var serverURL = "https://www.festivaltime.us/app/gametime_war/?callback=?";
    var networkTimeout = setTimeout(function () {
        jsonjqXHR.abort();
    }, 30000);
//    alert("Request sent to: " + serverURL);
    var jsonjqXHR = $.getJSON(serverURL, data, function (response) {
        v.forEach(function (element) {
            if (response.hasOwnProperty(element)) {
                if (response[element].hasOwnProperty("appDataTime")) {
                    var timeKey = element + "TimeStamp";
                    var timeVal = response[element]["appDataTime"];
                    localStorage.setItem(timeKey, timeVal);
                    delete response[element]["appDataTime"];
                }
                localStorage.setItem(element, JSON.stringify(response[element]));
                sortIncoming(element);


            }
        });
    }).always(function (d) {
        clearTimeout(networkTimeout);
    });
    return jsonjqXHR;
}

$(function () {
    var curFest = objectify("currentFestivalData");
    if (curFest) {
        $("#footer-text").html(curFest.sitename);
    }

    $("[data-role='header'], [data-role='footer']").toolbar();

//    getFramework();

//    if (!localStorage.setItem("reloadRequired", "false")) userLoggedIn("festival_select", "login");


    $(document).ajaxError(function (event, jqxhr, settings, thrownError) {

        alert("No network");

        alert(jqxhr.data.reqType);


        alert("Failed to connect to: " + settings.url);

    });

    $("#content").on('click', ".login_processing", function () {

        var form = {
            uname: $("#uname").val(),
            passwd: $("#passwd").val()
        };

        // request a login challenge for the username
        data = {
            reqType: "challenge_req",
            uname: form.uname
        };
        var reqChallenge = appServerReq(data);
        reqChallenge.done(function (d) {
            // receive salt and challenge, or that the username does not exist
            var data2;
            if (d.uname) {
                // if exists, concatenate submitted password with salt
                var escapedPW = mysqlEscape(form.passwd);
                var saltedPW = escapedPW + d.salt;
                // perform sha256 on salted password
                var hashedPW = sjcl.codec.hex.fromBits(sjcl.hash.sha256.hash(saltedPW));

                // concatenate hashed password with challenge
                var challPW = hashedPW + d.challenge;
                // hash challenged password
                var hashChallPW = sjcl.codec.hex.fromBits(sjcl.hash.sha256.hash(challPW));
                //        alert(hashChallPW);
                // send response
                data2 = {
                    reqType: "challenge_sub",
                    uname: d.uname,
                    response: hashChallPW
                };
                var reqResponse = appServerReq(data2);
                reqResponse.done(function (da) {
                    // receive either an auth token, or that the password is incorrect
                    if (da.pwValid) {
                        localStorage.setItem("mobile_auth_key", da.auth_key);
                        localStorage.setItem("uname", da.uname);
                        //                       alert("about to call festival_select");
                        callScreen("festival_select");

                        return true;
                    } else {
                        // if password is incorrect, suggest forgot password link or allow retry
                        callScreen("login_failed_password");
                        return true;
                    }
                }).fail(function (da) {
                    callScreen("login_failed_network");
                    return false;
                });
            } else {
                // if DNE, suggest creating an account or allow retry
                callScreen("login_failed_username");
                return false;
            }
        }).fail(function (d) {
            callScreen("login_failed_network");
            return false;
        });
        return false;
    });

    $("#content").on('click', ".select_purchased_festival", function () {

        var form = {
            select_purchased: $("#select-purchased").val()
        };

//        alert("Select Purchased Festival: " + form.select_purchased);

        // submit a festival to receive the gametime data
        var data = {
            reqType: "select_purchased",
            selectedFestival: form.select_purchased
        };
        var reqChallenge = appServerReq(data);

        reqChallenge.done(function (d) {
            callScreen("festival_home");
        }).fail(function (d) {
            callScreen("login_failed_network");
            return false;
        });
        return false;
    });

    $("#content").on('click', ".login", function () {
        callScreen("login");
        return false;
    });

    $("#content").on('click', ".login_failed_network", function () {
        callScreen("login_failed_network");
        return false;
    });

    $("#content").on('click', ".login_create", function () {
        callScreen("login_failed_create");
        return false;
    });

    $("#content").on('click', ".login_forgot_password", function () {
        callScreen("login_forgot_password");
        return false;
    });

    $("#user-overview-list").on('click', ".detail_button", function () {
        localStorage.setItem("currentUserDetail", $(this).data("user-id"));

    });

    $("#popupPanel").on('click', ".login", function () {
        callScreen("login");
        return false;
    });

    $("#popupPanel").on('click', ".festival_select", function () {
        callScreen("festival_select");
        return false;
    });


    $("#popupPanel").on({ popupbeforeposition: function () {
        var h = $(window).height();
        $("#popupPanel").css("height", h);
    }
    });


});




