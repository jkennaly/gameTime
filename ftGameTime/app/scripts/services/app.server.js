/**
 * Created by jbk on 10/5/14.
 */
angular.module('ftGameTimeApp')
    .factory('AppServer', ['$http', function AppServerFactory($http) {

        return {
            request: function (data) {
                //Determine if request type is a 'login'-type request
                var loginRequests = [
                    "login_status", "challenge_req", "challenge_sub"
                ];
//        console.log(data);
                var cred = {
                    mak: localStorage.getItem("mobile_auth_key"),
                    uname: localStorage.getItem("uname")
                };


                //add check for request type against loginRequests


                if (cred.uname) {
                    data.mobile_auth_key = cred.mak;
                    data.uname = cred.uname;
                } else if (loginRequests.indexOf(data.reqType) == -1) {
                    return false;
                }
                //       alert(data.uname);


                var serverURL = "http://localhost:8080/?callback=JSON_CALLBACK";
                var httpResponse = $http({
                    method: 'JSONP',
                    url: serverURL,
                    params: data
                });

                httpResponse.success(function (data) {
                    var v = [
                        "selfData",
                        "othersData",
                        "purchasedFestivals",
                        "unpurchasedFestivals",
                        "userFestivalData",
                        "currentFestivalData",
                        "bandFestivalData",
                        "setFestivalData",
                        "placeFestivalData",
                        "dayFestivalData",
                        "messageFestivalData",
                        "dateFestivalData"
                    ];
                    v.forEach(function (element) {
//            console.log(element);
                        if (data.hasOwnProperty(element)) {
                            if (data[element].hasOwnProperty("appDataTime")) {
                                var timeKey = element + "TimeStamp";
                                var timeVal = data[element]["appDataTime"];
                                localStorage.setItem(timeKey, timeVal);
                                delete data[element]["appDataTime"];
                            }
                            localStorage.setItem(element, JSON.stringify(data[element]));

                        }
                    });
                });


                return {
                    response: httpResponse
                }
            }
        }
    }]);
