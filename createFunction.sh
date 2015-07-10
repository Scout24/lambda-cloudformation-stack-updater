#!/bin/bash

aws lambda create-function \
--region eu-west-1 \
--function-name updateStackFunction \
--zip-file fileb://target/scala-2.11/lambda-test-assembly-1.0.0.jar \
--role arn:aws:iam::744969810879:role/lambda_basic_execution_all  \
--handler is24.StackUpdateHandler::handler \
--runtime java8 \
--timeout 15 \
--memory-size 512
