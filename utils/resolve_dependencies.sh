#!/bin/bash
# Download dependencies, retrying up to 5 times.
# This is a dirty hack to get around the issue that the Android SDK sometimes fails to download.
# See https://code.google.com/p/android/issues/detail?id=36941

# Accept the Android SDK license.
mkdir -p "$ANDROID_HOME/licenses"
echo -e "8933bad161af4178b1185d1a37fbf41ea5269c55" > "$ANDROID_HOME/licenses/android-sdk-license"

n=0
until [ $n -ge 5 ]
do
  # Break when successful.
  ./gradlew dependencies && break
  n=$[$n+1]
done
