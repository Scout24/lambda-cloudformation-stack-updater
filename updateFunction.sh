#!/bin/bash

aws lambda update-function-code \
--region eu-west-1 \
--function-name updateStackFunction \
--zip-file fileb://target/scala-2.11/lambda-test-assembly-1.0.0.jar 
