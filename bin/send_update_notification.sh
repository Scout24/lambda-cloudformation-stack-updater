#!/bin/bash
stack=$1
version=$2

if [[ -z $1 || -z $2 ]]
then
	echo "usage: $0 <stackName> <version>"
	exit 1
fi

aws sns publish --topic-arn arn:aws:sns:eu-west-1:744969810879:stackUpdateMessages \
    --message "{ \"stackName\": \"perf-6\", \"region\": \"eu-central-1\", \"params\": { \"dockerImageVersion\": \"$version\"}}"
