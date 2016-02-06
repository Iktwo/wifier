#!/bin/bash
echo "Downloading oui.txt"
curl -o oui.txt.new http://standards-oui.ieee.org/oui.txt
grep "^\w\w-\w\w-\w\w" oui.txt.new | sed 's#-#:#; s#-#:#; s#(hex)##; s#\s##; s#\s##; s#\s##; s#\s##; s#\s# #' | sort > oui.txt.new.tmp
mv oui.txt.new.tmp oui.txt.new

if [ -f "oui.txt" ]; then
    OLD_OUIS=`wc -l oui.txt | grep -o '[0-9]*'`
    NEW_OUIS=`wc -l oui.txt.new | grep -o '[0-9]*'`

    if (( $NEW_OUIS <= $OLD_OUIS )); then
        rm data/data.json
        echo "No new ouis detected"
	exit 0
    fi
else
    mv oui.txt.new oui.txt
    echo "oui.txt updated"
    exit 0
fi

NEW_DATA=$(( NEW_OUIS - OLD_OUIS))
echo "$NEW_DATA New ouis detected"

read -p "Would you like to update the current list? " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    mv oui.txt.new oui.txt
    echo "oui.txt updated"
fi

