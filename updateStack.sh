#!/bin/bash
stack=$1
version=$2

aws sns publish --topic-arn arn:aws:sns:eu-central-1:744969810879:lambdatest --message "{\"default\": \"{\"stackName\": \"perf-6\", \"version\": \"21\"}\", \"lambda\": \"{\"stackName\": \"perf-6\", \"version\": \"21\"}\", \"APNS_VOIP_SANDBOX\": \"{\"aps\":{\"alert\": \"{\"stackName\": \"$stack\", \"version\": \"$version\"}\"} }\"}"

