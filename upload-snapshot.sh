#!/bin/bash

if gradle jar | grep 'Building snapshot'; then
  echo 'Uploading SNAPSHOT'
  gradle uploadArchives
fi
