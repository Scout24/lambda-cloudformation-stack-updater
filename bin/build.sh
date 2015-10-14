#! /bin/bash

scriptdir=bin
app_home=.
version=$1
sbtLauncherJar=sbt-launch-0.13.8.jar
case $version in
  ''|*[!0-9.]*)
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
  $SBT_OPTS"

java $sbt_opts -jar $scriptdir/$sbtLauncherJar clean test && echo -n "version := \"$version\"" > $app_home/version.sbt && java $sbt_opts -jar $scriptdir/$sbtLauncherJar assembly


#  -Dhttp.proxyHost=proxy.rz.is24.loc \
#  -Dhttp.proxyPort=3128"
