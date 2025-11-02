#!/bin/bash
GIT_COMMIT_ID=`git rev-parse --short --verify HEAD 2>/dev/null`
MODIFIED=`git status --short 2>/dev/null`
LAST_TAG=`git describe --tags --exact-match 2>/dev/null`

#echo "GIT_COMMIT_ID=$GIT_COMMIT_ID"
#echo "MODIFIED=$MODIFIED"
#echo "LAST_TAG=$LAST_TAG"

if [ "$GIT_COMMIT_ID" == "" ]; then
  GIT_COMMIT_ID='(git error)'
else
  if [ -z "$MODIFIED" ]; then
    # not modified version
    if [$LAST_TAG == '']; then
      : # no tag - keep GIT_COMMIT_ID
    else
      GIT_COMMIT_ID=$LAST_TAG
    fi
  else
    # modified content
    GIT_COMMIT_ID="modified-$GIT_COMMIT_ID"
  fi
fi

rm -rf src/version/VersionInfo.java 2>/dev/null
mkdir src/version 2>/dev/null

sed s/git-revision/$GIT_COMMIT_ID/ <version.template > src/version/VersionInfo.java

echo "$0: Resulted GIT_COMMIT_ID=$GIT_COMMIT_ID"
