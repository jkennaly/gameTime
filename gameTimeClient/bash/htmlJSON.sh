#!/bin/bash
FILES=*.frag.html
echo -n "{" > html.json
delim=''
for f in $FILES
do
    echo "Processing $f file..."
    page="${f%%.frag.html*}"
    page=${page//./_}
    echo "$delim" >> html.json
    delim=','
    echo -n "\"$page\" : \" " >> html.json
    sed 's/\"/\\"/g' $f > tmp.out
    java -jar ../res/htmlcompressor-1.5.3.jar  tmp.out >> html.json
    echo -n "\"" >> html.json
done
echo "" >> html.json
echo -n "}" >> html.json
rm tmp.out
mv html.json ../html.json