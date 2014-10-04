<?php
include_once("php/var.php");
?>

<!DOCTYPE html>
<!--[if lt IE 7]>
<html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>
<html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>
<html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->
<html class="no-js"> <!--<![endif]-->
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <title>GameTime</title>
    <meta name="description" content="">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!--
            <link rel="stylesheet" href="css/normalize.min.css">
            <link rel="stylesheet" href="css/main.css">
    -->
    <link rel="stylesheet" href="css/jquery.mobile-1.4.4.min.css">
    <link rel="stylesheet" href="css/style.css">
    <script src="js/vendor/jquery-1.11.0.min.js"></script>
    <script src="js/vendor/jquery.mobile-1.4.4.js"></script>
    <script src="js/plugins.js"></script>
    <script src="js/src/main.js"></script>
    <script src="js/vendor/modernizr-2.6.2-respond-1.1.0.min.js"></script>
    <script src="js/vendor/sjcl.js"></script>
    <!-- Load the script "js/main.js" as our entry point -->
    <script data-main="js/main" src="js/vendor/require.js"></script>
</head>
<body>

<!--[if lt IE 7]>
<p class="browsehappy">You are using an <strong>outdated</strong> browser. Please <a href="http://browsehappy.com/">upgrade
    your browser</a> to improve your experience.</p>
<![endif]-->


<div data-role="content">
    <div class="main wrapper clearfix">
        <div id="content">

            <?php require_once "php/header.php"; ?>
            <div data-role="page" id="default">

                <a href="index.php#login" data-role="button" data-rel="dialog" data-transition="pop"
                   data-inline="true" data-corners="true" data-shadow="true" data-iconshadow="true"
                   data-wrapperels="span"
                   data-theme="c" class="ui-btn ui-shadow ui-btn-corner-all ui-btn-inline ui-btn-up-c">
        <span class="ui-btn-inner ui-btn-corner-all">
            <span class="ui-btn-text">Log In</span>
        </span>
                </a>
                <nav>
                    <ul>
                        <li><a href="#">nav ul li a</a></li>
                        <li><a href="#">nav ul li a</a></li>
                        <li><a href="#">nav ul li a</a></li>
                    </ul>
                </nav>
                <article>
                    <header>
                        <h1>article header h1</h1>

                        <p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam sodales urna non odio
                            egestas tempor. Nunc vel vehicula ante. Etiam bibendum iaculis libero, eget molestie nisl
                            pharetra in. In semper consequat est, eu porta velit mollis nec.</p>
                    </header>

                    <section>
                        <h2>article section h2</h2>

                        <p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam sodales urna non odio
                            egestas tempor. Nunc vel vehicula ante. Etiam bibendum iaculis libero, eget molestie nisl
                            pharetra in. In semper consequat est, eu porta velit mollis nec. Curabitur posuere enim eget
                            turpis feugiat tempor. Etiam ullamcorper lorem dapibus velit suscipit ultrices. Proin in est
                            sed erat facilisis pharetra.</p>
                    </section>
                    <section>
                        <h2>article section h2</h2>

                        <p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam sodales urna non odio
                            egestas tempor. Nunc vel vehicula ante. Etiam bibendum iaculis libero, eget molestie nisl
                            pharetra in. In semper consequat est, eu porta velit mollis nec. Curabitur posuere enim eget
                            turpis feugiat tempor. Etiam ullamcorper lorem dapibus velit suscipit ultrices. Proin in est
                            sed erat facilisis pharetra.</p>
                    </section>
                    <footer>
                        <h3>article footer h3</h3>

                        <p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam sodales urna non odio
                            egestas tempor. Nunc vel vehicula ante. Etiam bibendum iaculis libero, eget molestie nisl
                            pharetra in. In semper consequat est, eu porta velit mollis nec. Curabitur posuere enim eget
                            turpis feugiat tempor.</p>
                    </footer>
                </article>

                <aside>
                    <h3>aside</h3>

                    <p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam sodales urna non odio egestas
                        tempor. Nunc vel vehicula ante. Etiam bibendum iaculis libero, eget molestie nisl pharetra in.
                        In semper consequat est, eu porta velit mollis nec. Curabitur posuere enim eget turpis feugiat
                        tempor. Etiam ullamcorper lorem dapibus velit suscipit ultrices.</p>
                </aside>


            </div>
            <!-- /page -->
            <?php require_once "php/pages.php"; ?>
            <?php require_once "php/footer.php"; ?>
        </div>
        <!-- #content -->
    </div>
    <!-- #main -->
</div>
<!-- #main-container -->


<!-- Google Analytics: change UA-XXXXX-X to be your site's ID. -->
<script>
    (function (b, o, i, l, e, r) {
        b.GoogleAnalyticsObject = l;
        b[l] || (b[l] =
            function () {
                (b[l].q = b[l].q || []).push(arguments)
            });
        b[l].l = +new Date;
        e = o.createElement(i);
        r = o.getElementsByTagName(i)[0];
        e.src = '//www.google-analytics.com/analytics.js';
        r.parentNode.insertBefore(e, r)
    }(window, document, 'script', 'ga'));
    ga('create', 'UA-XXXXX-X');
    ga('send', 'pageview');
    var serverURL = "<?php echo $serverURL; ?>";
</script>
</body>
</html>
