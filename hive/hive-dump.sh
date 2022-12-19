#!/bin/sh
clean=false
while getopts ':c:h:' opt; do
    case $opt in
        c) clean=true;;
        h) echo "Usage: ./hive-dump.sh [-c 1 | -h 1]"; exit;;
        *) echo "Error unknown arg!" exit 1
    esac
done
echo configuring hive ...
if "$clean";
    then if [[ $(echo "show tables like 'tweets'" | hive | grep 'tweets') ]];
        then echo "deleting tweets table"
        hive -e "drop table tweets"
        hive -f hive.hql;
    else
        echo "clean else"
        hive -f hive.hql;
    fi
else
    echo "else"
    hive -f hive.hql;
fi