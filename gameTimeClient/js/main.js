/**
 * Created by jbk on 10/4/14.
 */
// Filename: main.js

// Require.js allows us to configure shortcut alias
// There usage will become more apparent further along in the tutorial.
require.config({
    paths: {
        jquerymobile: 'vendor/jquery.mobile-1.4.4.js',
        jquery: 'vendor/jquery-1.11.0.min.js',
        sjcl: 'vendor/sjcl.js'
    }

});

require([

    // Load our app module and pass it to our definition function
    'src/gametime'
], function (Gametime) {
    // The "app" dependency is passed in as "App"
    Gametime.initialize();
});
