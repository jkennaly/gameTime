/**
 * Created by jbk on 10/6/14.
 */

angular.module('ftGameTimeApp')
    .factory('BandCardsCurrent', function BandCardsCurrentFactory(FestivalMessage, FestivalFestival, Set) {

        return {
            cards: function (bandId) {
//                console.log(bandId);

                var cards = [];
                var authors = [];
                var sets = [];
                var messages = [];
                var i;
                for (i = 0; i < FestivalMessage.length; i++) {
                    if (FestivalMessage[i].band == bandId) {
                        messages.push(i);
                        //get the author
                        var author = FestivalMessage[i].fromuser;
                        if (authors.indexOf(author) == -1) {
                            authors.push(author);
                        }
                        if (FestivalMessage[i].mode == 2) {
                            var set = FestivalMessage[i].set;
                            if (sets.indexOf(set) == -1 && set > 0) sets.push(set);
                        }
                    }

                }
                authors.sort();
                sets.sort();
                messages.sort();

//                 console.log(authors);
//                 console.log(sets);
//                 console.log(messages);

                for (i = 0; i < authors.length; i++) {
                    var card = [];
                    card.reportAuthor = authors[i];
                    card.festival = FestivalFestival.sitename;
                    card.pgComment = "";
                    card.gtComment = "";
                    var currentSet = 0;
                    var collectedSets = [];
                    //Loop through the messages until until an entire loop passes without a rating for a new set

                    do {
                        for (var j = 0; j < messages.length; j++) {
                            var msg = FestivalMessage[messages[j]];
//                            console.log("index", j);
//                            console.log("incomingMsg", msg);
                            if (msg.fromuser == card.reportAuthor) {
                                if (msg.mode == 1 && msg.remark == 1) {
                                    card.pgComment += msg.content + "\n";
//                                    console.log("pgComment", msg);
                                }
                                if (msg.mode == 1 && msg.remark == 2) {
                                    card.pgRating = msg.content;
//                                    console.log("pgRating", msg);
                                }
                                if (msg.mode == 2 && msg.remark == 1) {
                                    if (msg.set == currentSet || (currentSet == 0 && collectedSets.indexOf(msg.set) == -1)) {
                                        card.gtComment += msg.content + "\n";
                                        currentSet = msg.set;
//                                        console.log("gtComment", msg);
                                    }
                                }
                                if (msg.mode == 2 && msg.remark == 2) {
                                    if (msg.set == currentSet || (currentSet == 0 && collectedSets.indexOf(msg.set) == -1)) {
                                        card.gtRating = msg.content;
                                        currentSet = msg.set;
//                                        console.log("gtRating", msg);
                                    }

                                }
                            }
                            if (currentSet > 0) {
                                var set = new Set(currentSet);
                                card.festival = FestivalFestival.sitename + " " + set.day + " " + set.stage;
                                cards.push(card);
                                collectedSets.push(currentSet);
                                currentSet = 0;
                            }
                        }
                    } while (currentSet > 0);
                    if (collectedSets.length == 0) cards.push(card);
                }
                return cards;
            }

        }

    });
