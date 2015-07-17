# deployment

...

# send notification

    aws sns publish --topic-arn arn:aws:sns:eu-west-1:744969810879:stackUpdateMessages \
        --message "{ \"stackName\": \"perf-6\", \"region\": \"eu-central-1\", \"params\": { \"dockerImageVersion\": \"$version\"}}"
