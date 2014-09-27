var v = [
    "selfData",
    "othersData",
    "purchasedFestivals",
    "unpurchasedFestivals",
    "userFestivalData",
    "currentFestivalData"
];

function getAuth(){
    var credentials = {
        mak:    localStorage.getItem( "mobile_auth_key" ),
        uname:  localStorage.getItem( "uname" )
    };

    if(!credentials.mak || !credentials.uname) return false;

    return credentials;
}

function mysqlEscape(stringToEscape){
    return stringToEscape
        .replace("\\", "\\\\")
        .replace("\'", "\\\'")
        .replace("\"", "\\\"")
        .replace("\n", "\\\n")
        .replace("\r", "\\\r")
        .replace("\x00", "\\\x00")
        .replace("\x1a", "\\\x1a");
}

function userLoggedIn( success, failure){
    var credPresent = true;
    if(!getAuth()) credPresent = false;
    if(!credPresent) {
        callScreen(failure);
        return false;
    }
    var data = {
        reqType: "login_status",
        mobile_auth_key: credPresent.mak,
        uname: credPresent.uname
    };
    var logcheck = appServerReq(data, true);
    logcheck.done(function(d) {
        if(d.loginValid) {
            callScreen(success);
            return true;
        } else{
            callScreen(failure);
            return false;
        }
    }).fail(function(d) {
        callScreen(success);
        return true;
    });
}

function objectify(lsObject) {
    return JSON.parse(localStorage.getItem(lsObject));
}

function drawFestHome() {
    var curFest = objectify("currentFestivalData");
//            alert(purFests);
    $("#footer-text").html(curFest.sitename);

}

function drawFestSelect() {
    var purFests = JSON.parse(localStorage.getItem("purchasedFestivals"));
//            alert(purFests);
    var $el = $("#select-purchased");
    $el.empty(); // remove old options
    purFests.forEach(function (element) {
//        alert(element.sitename);
        $el.append($("<option></option>")
            .attr("value", element.id).text(element.sitename));
    });
}

function callScreen(screen) {
    $("#content").html(localStorage.getItem(screen));
    switch (screen) {
        case "festival_select":
            drawFestSelect();
            break;
        case "festival_home":
            drawFestHome();
            break;
        default:
            break;
    }
    return false;
}

function appServerReq(data, showProcessing){
    //Determine if request type is a 'login'-type request
    var loginRequests = [
        "login_status", "challenge_req", "challenge_sub"
    ];

    var cred = getAuth();

    //add check for request type against loginRequests

    if(cred != false){
        data.mobile_auth_key = cred.mak;
        data.uname = cred.uname;
    }

    //If there are credentials present, add credentials to submission
    //(unless request type is login_status, challenge_req, or challenge_sub

    //If there are not credentials present, redirect to login screen without executing request
    //(unless request type is login_status, challenge_req, or challenge_sub

//    var serverURL = "https://www.festivaltime.us/app/gametime_war/?callback=?";
    var networkTimeout = setTimeout(function() { jsonjqXHR.abort(); }, 30000);
//    alert("Request sent to: " + serverURL);
    var jsonjqXHR = $.getJSON(serverURL, data, function (response) {
        v.forEach(function (element) {
            if (response.hasOwnProperty(element)) localStorage.setItem(element, JSON.stringify(response[element]));
        });
    }).always(function(d) {
        clearTimeout(networkTimeout);
    });

    if(showProcessing) {
        callScreen("request_processing");
    }
    return jsonjqXHR;
}

$(function () {
    var curFest = objectify("currentFestivalData");
    if (curFest) {
        $("#footer-text").html(curFest.sitename);
    }


    $.get('html.json', function (response) {
        localStorage.setItem("reloadRequired", "false");
        for (var i in response) {

            if (response.hasOwnProperty(i)) {  // this validates if prop belongs to obj
                localStorage.setItem(i, response[i]);
            }
        }
    }).always(function(d) {
        if (!curFest) userLoggedIn("festival_select", "login");
        else userLoggedIn("festival_home", "login");
    });

    if(!localStorage.setItem("reloadRequired", "false")) userLoggedIn("festival_select", "login");


    $( document ).ajaxError(function( event, jqxhr, settings, thrownError ) {

        alert("No network");

            alert(jqxhr.data.reqType);


        alert ( "Failed to connect to: " + settings.url);

    });

    $("#content").on( 'click', ".login_processing", function(){

        var form = {
            uname:  $( "#uname").val(),
            passwd: $( "#passwd").val()
        };
        $( "#content" ).html( localStorage.getItem( "request_processing" ));

        // request a login challenge for the username
        data = {
            reqType:    "challenge_req",
            uname:      form.uname
        };
        var reqChallenge = appServerReq(data, true);


        reqChallenge.done(function(d) {
            // receive salt and challenge, or that the username does not exist
            if(d.uname) {
                // if exists, concatenate submitted password with salt
                $( "#content" ).html( localStorage.getItem( "request_processing" ) );
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
                    reqType:    "challenge_sub",
                    uname:      d.uname,
                    response:   hashChallPW
                };
                var reqResponse = appServerReq(data2, true);
                reqResponse.done(function (da){
                    // receive either an auth token, or that the password is incorrect
                    if(da.pwValid){
                        localStorage.setItem( "mobile_auth_key", da.auth_key );
                        localStorage.setItem( "uname", da.uname );
                        callScreen("festival_select");

                        return true;
                    } else {
                        // if password is incorrect, suggest forgot password link or allow retry
                        callScreen("login_failed_password");
                        return true;
                    }
                }).fail(function(da){
                    callScreen("login_failed_network");
                    return false;
                });
            } else{
                // if DNE, suggest creating an account or allow retry
                callScreen("login_failed_username");
                return false;
            }
        }).fail(function(d) {
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
        data = {
            reqType: "select_purchased",
            selectedFestival: form.select_purchased
        };
        var reqChallenge = appServerReq(data, true);

        reqChallenge.done(function (d) {
            callScreen("festival_home");
        }).fail(function (d) {
            callScreen("login_failed_network");
            return false;
        });
        return false;
    });

    $("#content").on( 'click', ".login", function(){
        callScreen("login");
        return false;
    });

    $("#content").on( 'click', ".login_failed_network", function(){
        callScreen("login_failed_network");
        return false;
    });

    $("#content").on( 'click', ".login_create", function(){
        callScreen("login_failed_create");
        return false;
    });

    $("#content").on( 'click', ".login_forgot_password", function(){
        callScreen("login_forgot_password");
        return false;
    });


});




