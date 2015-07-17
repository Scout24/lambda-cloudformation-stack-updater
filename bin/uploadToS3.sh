#!/bin/bash
version=$1

if [ -z "$version" ]; then
    echo "usage: $0 version"
    exit 1
fi

aws s3 cp $(find target -name lambda-cloudFormation-stack-updater-$version.jar) s3://de.is24.val.update-stack-function
