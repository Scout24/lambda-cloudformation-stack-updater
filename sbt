#!/bin/bash

SBT_OPTS="-Dfile.encoding=UTF8 -Xms128M -Xmx1012M -Xss1M -XX:+CMSClassUnloadingEnabled"
ARG=$1
DEBUG=""
if [ "$ARG" = "debug" ]; then
  echo "Debug modus"
  DEBUG="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=9999"
  shift
fi
NO_FORMAT=""
if [ "$ARG" = "noformat" ]; then
  NO_FORMAT="-Dsbt.log.noformat=true"
  shift
else
  SBT_OPTS="$SBT_OPTS -Dscala.color"
fi


if [ -d .git ]; then
  if [ ! -f .git/hooks/pre-push ]; then
    echo Linking git pre-push hook
    ln -s `pwd`/git-hooks/pre-push .git/hooks/
  fi
fi

java $DEBUG $NO_FORMAT $SBT_OPTS -jar bin/sbt-launch-0.13.8.jar "$@"
