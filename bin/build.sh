#! /bin/bash

script=$(readlink -f "$0")
scriptdir=$(dirname "$script")
app_home=$(readlink -f "$scriptdir/..")
version=$1
sbtLauncherJar=sbt-launch-0.13.8.jar
case $version in
  ''|*[!0-9]*)
    echo "usage: build.sh versionNumber"
    exit 1
    ;;
  *)
    ;;
esac


sbt_opts="\
  -Dsbt.log.noformat=true \
  -Xms512M -Xmx1536M -Xss1M \
  -XX:+CMSClassUnloadingEnabled \
  -Dsbt.repository.config=sbt-settings/repositories \
  -Dhttp.proxyHost=proxy.rz.is24.loc \
  -Dhttp.proxyPort=3128"

find . -name "*.sbt" -exec sed -i 's/0.0.0-SNAPSHOT/[1,)/g' {} \;

java $sbt_opts -jar $scriptdir/$sbtLauncherJar clean test && echo -n "app.version=$version" > $app_home/src/main/resources/version.conf && java $sbt_opts -jar $scriptdir/$sbtLauncherJar docker:publish
