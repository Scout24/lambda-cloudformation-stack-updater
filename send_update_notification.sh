#!/bin/bash
stack=$1
version=$2

aws sns publish --topic-arn arn:aws:sns:eu-west-1:744969810879:stackUpdateMessages \
    --message '{ "stackName": "perf-6", "region": "eu-central-1", "params": { "dockerImageVersion": "30"}}'

# { "stackName": "perf-6", "region": "eu-central-1", "params": { "dockerImageVersion": "27"}}

# {"default": "{ \"stackName\": \"perf-6\", \"region\": \"eu-central-1\", \"params\": { \"dockerImageVersion\": \"28\"}}","lambda": "{ \"stackName\": \"perf-6\", \"region\": \"eu-central-1\", \"params\": { \"dockerImageVersion\": \"28\"}}"}