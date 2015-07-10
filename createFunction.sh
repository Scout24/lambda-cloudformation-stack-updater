#!/bin/bash

aws lambda create-function \
--region eu-west-1 \
--function-name getting-started-lambda-function-in-java2 \
--zip-file fileb://target/scala-2.11/lambda-test-assembly-1.0.0.jar \
--role arn:aws:iam::744969810879:role/lambda_basic_execution  \
--handler is24.Handler::myHandler \
--runtime java8 \
--timeout 15 \
--memory-size 512
