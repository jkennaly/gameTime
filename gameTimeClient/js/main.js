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
    if(!getAuth()) {
        $( "#content" ).html( localStorage.getItem(failure) );
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
            $( "#content" ).html( localStorage.getItem(success) );
            return true;
        } else{
            $( "#content" ).html( localStorage.getItem(failure) );
            return false;
        }
    }).fail(function(d) {
        $( "#content" ).html( localStorage.getItem(success) );
        return true;
    });
}

function appServerReq(data, showProcessing){
    var serverURL = "https://festivaltime.us/app?callback=?";
    var networkTimeout = setTimeout(function() { jsonjqXHR.abort(); }, 3000);
    var jsonjqXHR = $.getJSON(serverURL, data, function (response) {

    }).always(function(d) {
        clearTimeout(networkTimeout);
    });

    if(showProcessing) {
        $( "#content" ).html( localStorage.getItem( "request_processing" ));
    }
    return jsonjqXHR;
}



$(function () {
    var pageLoaded;

    $.get('html.json', function (response) {
        localStorage.setItem("reloadRequired", "false");
        for (var i in response) {

            if (response.hasOwnProperty(i)) {  // this validates if prop belongs to obj
                localStorage.setItem(i, response[i]);
            }
        }
    }).always(function(d) {
        if(!pageLoaded) userLoggedIn("festival_select", "login");
    });

    if(!localStorage.setItem("reloadRequired", "false")) userLoggedIn("festival_select", "login");


    $( document ).ajaxError(function( event, jqxhr, settings, thrownError ) {

        alert("No network");
        /*
            alert(jqxhr.data.reqType);


        alert ( "Failed to connect to: " + settings.url);
*/
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

                        $( "#content" ).html( localStorage.getItem( "festival_select") );
                        return true;
                    } else {
                        // if password is incorrect, suggest forgot password link or allow retry
                        $( "#content" ).html( localStorage.getItem( "login_failed_password") );
                        return true;
                    }





                }).fail(function(da){
                    $( "#content" ).html( localStorage.getItem( "login_failed_network") );
                    return false;
                });

            } else{
                // if DNE, suggest creating an account or allow retry
                $( "#content" ).html( localStorage.getItem("login_failed_username") );
                return false;
            }
        }).fail(function(d) {
            $( "#content" ).html( localStorage.getItem( "login_failed_network") );
            return false;
        });
        return false;
    });

    $("#content").on( 'click', ".login", function(){
        $( "#content" ).html( localStorage.getItem( "login" ));
        return false;
    });

    $("#content").on( 'click', ".login_failed_network", function(){
        $( "#content" ).html( localStorage.getItem( "login_failed_network" ));
        return false;
    });

    $("#content").on( 'click', ".login_create", function(){
        $( "#content" ).html( localStorage.getItem( "login_create" ));
        return false;
    });

    $("#content").on( 'click', ".login_forgot_password", function(){
        $( "#content" ).html( localStorage.getItem( "login_forgot_password" ));
        return false;
    });


});




