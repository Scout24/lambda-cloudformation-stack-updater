#!/bin/bash
version=$1

if [ -z "$version" ]; then
    echo "usage: $0 version"
    exit 1
fi

aws s3 cp --acl public-read --region eu-west-1 $(find target -name lambda-cloudFormation-stack-updater-$version.jar) s3://de.is24.val.update-stack-function
