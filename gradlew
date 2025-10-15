#!/usr/bin/env sh

#
# Copyright 2015 the original author or authors.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

#*****************************************************************************
#
#   Gradle start up script for UN*X
#
#*****************************************************************************

# Add default JVM options here. You can also use JAVA_OPTS and GRADLE_OPTS to pass JVM options to this script.
DEFAULT_JVM_OPTS='"-Xmx64m" "-Xms64m"'

APP_NAME="Gradle"
APP_BASE_NAME=`basename "$0"`

# Use the maximum available path length for the Java command
if [ -z "$MAX_PATH" ]; then
    MAX_PATH=4096
fi

# For Cygwin, ensure paths are in UNIX format before anything is touched
if $cygwin; then
    [ -n "$JAVA_HOME" ] && JAVA_HOME=`cygpath --unix "$JAVA_HOME"`
fi

# Attempt to set APP_HOME
# Resolve links: $0 may be a link
PRG="$0"
# Need this for relative symlinks.
while [ -h "$PRG" ] ; do
    ls=`ls -ld "$PRG"`
    link=`expr "$ls" : '.*-> \(.*\)$'`
    if expr "$link" : '/.*' > /dev/null; then
        PRG="$link"
    else
        PRG=`dirname "$PRG"`"/$link"
    fi
done

APP_HOME=`dirname "$PRG"`

# Absolutize APP_HOME
# Mac OS X readlink is not GNU readlink
if [ `uname -s` = "Darwin" ]; then
    eval `echo "APP_HOME=\"`cd \\\"\$APP_HOME\\\" && pwd -P\"`" 
else 
    APP_HOME=`readlink -f "$APP_HOME"`
fi

# Add the gradlew properties file
if [ -f "$APP_HOME/gradle/wrapper/gradle-wrapper.properties" ]; then
    . "$APP_HOME/gradle/wrapper/gradle-wrapper.properties"
fi

# Make sure the distribution base is set
if [ -z "$distributionBase" ]; then
    distributionBase="GRADLE_USER_HOME"
fi

# Make sure the distribution path is set
if [ -z "$distributionPath" ]; then
    distributionPath="wrapper/dists"
fi

# Make sure the zip store base is set
if [ -z "$zipStoreBase" ]; then
    zipStoreBase="GRADLE_USER_HOME"
fi

# Make sure the zip store path is set
if [ -z "$zipStorePath" ]; then
    zipStorePath="wrapper/dists"
fi

# Make sure the distribution URL is set
if [ -z "$distributionUrl" ]; then
    echo "ERROR: Could not find a value for distributionUrl in \"$APP_HOME/gradle/wrapper/gradle-wrapper.properties\"" 1>&2
    exit 1
fi

# Determine the Gradle user home directory.
if [ "$distributionBase" = "GRADLE_USER_HOME" ]; then
    if [ -z "$GRADLE_USER_HOME" ]; then
        GRADLE_USER_HOME="$HOME/.gradle"
    fi
    GRADLE_DIST_BASE="$GRADLE_USER_HOME"
    GRADLE_ZIP_STORE_BASE="$GRADLE_USER_HOME"
elif [ "$distributionBase" = "PROJECT" ]; then
    GRADLE_DIST_BASE="$APP_HOME"
    GRADLE_ZIP_STORE_BASE="$APP_HOME"
else
    echo "ERROR: Unrecognized value for distributionBase in \"$APP_HOME/gradle/wrapper/gradle-wrapper.properties\": '$distributionBase'" 1>&2
    exit 1
fi

# Determine the zip store directory.
if [ "$zipStoreBase" = "GRADLE_USER_HOME" ]; then
    if [ -z "$GRADLE_USER_HOME" ]; then
        GRADLE_USER_HOME="$HOME/.gradle"
    fi
    GRADLE_ZIP_STORE="$GRADLE_USER_HOME/$zipStorePath"
elif [ "$zipStoreBase" = "PROJECT" ]; then
    GRADLE_ZIP_STORE="$APP_HOME/$zipStorePath"
else
    echo "ERROR: Unrecognized value for zipStoreBase in \"$APP_HOME/gradle/wrapper/gradle-wrapper.properties\": '$zipStoreBase'" 1>&2
    exit 1
fi

# Construct the distribution and zip file names
distributionUrlPath=`echo $distributionUrl | sed -e 's/\\:/_/' -e 's/:/_/g'`
baseName=`basename "$distributionUrl"`
extension="${baseName##*.}"
baseName="${baseName%.*}"
distDir="$GRADLE_DIST_BASE/$distributionPath/$baseName"
distFile="$GRADLE_ZIP_STORE/$distributionUrlPath/$baseName.$extension"

# For Cygwin, switch paths to Windows format before running java
if $cygwin; then
    APP_HOME=`cygpath --path --windows "$APP_HOME"`
    JAVA_HOME=`cygpath --path --windows "$JAVA_HOME"`
    CLASSPATH=`cygpath --path --windows "$CLASSPATH"`
fi

# Setup the command-line arguments for gradle.
GRADLE_CMD_LINE_ARGS=() 
for i in "$@" ; do
    GRADLE_CMD_LINE_ARGS=("${GRADLE_CMD_LINE_ARGS[@]}" "$i") 
done

# Find the Java command
if [ -n "$JAVA_HOME" ] ; then
    if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
        # IBM's JDK on AIX uses strange locations for the executables
        JAVACMD="$JAVA_HOME/jre/sh/java"
    else
        JAVACMD="$JAVA_HOME/bin/java"
    fi
    if [ ! -x "$JAVACMD" ] ; then
        die "ERROR: JAVA_HOME is set to an invalid directory: $JAVA_HOME\n\nPlease set the JAVA_HOME variable in your environment to match the\nlocation of your Java installation."
    fi
else
    JAVACMD="java"
    which java >/dev/null 2>&1 || die "ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.\n\nPlease set the JAVA_HOME variable in your environment to match the\nlocation of your Java installation."
fi

# Don't pass the args to the wrapper if we are in execute-wrapper mode.
if [ "$1" != "--execute-wrapper" ]; then
    # Download the distribution if not already present
    if [ ! -f "$distFile" ]; then
        echo "Downloading $distributionUrl"
        if [ ! -d `dirname "$distFile"` ]; then
            mkdir -p `dirname "$distFile"`
        fi
        if [ `command -v "wget"` ]; then
            wget -q -O "$distFile" "$distributionUrl"
        elif [ `command -v "curl"` ]; then
            curl -# -L -f -o "$distFile" "$distributionUrl"
        else 
            echo "ERROR: Neither wget or curl is available" 1>&2
            exit 1
        fi
    fi

    # Unpack the distribution if not already present
    if [ ! -d "$distDir" ]; then
        echo "Unzipping $distFile to $distDir"
        if [ ! -d "$distDir" ]; then
            mkdir -p "$distDir"
        fi
        unzip -q -d "$distDir" "$distFile"
    fi

    # Find the gradle launcher JAR
    gradleLauncherJar=`find "$distDir" -name "gradle-launcher-*.jar"`

    # Set the classpath
    CLASSPATH="$gradleLauncherJar"
fi

# Split up the JVM options only if it is not protected by double quotes
if [ -z "${GRADLE_OPTS%\"*}" ] ; then
    JVM_OPTS=($GRADLE_OPTS)
else
    JVM_OPTS="$GRADLE_OPTS"
fi

# Add default JVM options if there are any
if [ -n "$DEFAULT_JVM_OPTS" ]; then
    JVM_OPTS=($JVM_OPTS $DEFAULT_JVM_OPTS)
fi

# Add the JAVA_OPTS to the JVM options
if [ -n "$JAVA_OPTS" ]; then
    JVM_OPTS=($JVM_OPTS $JAVA_OPTS)
fi

# Execute Gradle
exec "$JAVACMD" "${JVM_OPTS[@]}" -classpath "$CLASSPATH" org.gradle.launcher.GradleMain "${GRADLE_CMD_LINE_ARGS[@]}"
