#!/bin/bash
stackname=$1
jarname=$2

if [[ -z $stackname || -z $jarname ]]
then
        echo "usage: $0 <stackname> <jarname>"
        exit 1
fi

aws cloudformation update-stack --region eu-west-1 --stack-name $stackname --use-previous-template \
    --capabilities "CAPABILITY_IAM" \
    --parameters "ParameterValue=$jarname,ParameterKey=jarName"
