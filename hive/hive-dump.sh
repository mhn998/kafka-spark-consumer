#!/bin/sh

clean=false

while getopts 'c' opt; do
	case $opt in
		c) clean=true;;
		*) echo "Error unknown arg!" exit 1
	esac
done


echo configuring hive ...
if "$clean";
	then if [[ $(echo "show tables like 'tweets'" | hive | grep 'tweets') ]];
		then echo "deleting tweets table"
		hive -e "drop table tweets"
		hive -f ../confg/hive.hql;
	else
		hive -f ../confg/hive.hql;
	fi
else
	hive -f ../confg/hive.hql;
fi






