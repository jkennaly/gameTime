<?php
/**
 * Created by IntelliJ IDEA.
 * User: jbk
 * Date: 9/27/14
 * Time: 4:56 PM
 */
$old_path = getcwd();
chdir($pagesDir);
$content_files = glob("*.frag.html");

foreach ($content_files as $file) {
    $htmlFrag = fopen($file, "r");
    $info = '';
    while (!feof($htmlFrag)) {
        $info .= fgets($htmlFrag);
    }
    echo $info;
}
chdir($old_path);
//var_dump($content_files);
