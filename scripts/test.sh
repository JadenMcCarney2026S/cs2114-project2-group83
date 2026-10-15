#!/bin/sh
# Runs the course-standard student.TestCase tests using CS2-Support/student.jar.
set -eu
cd "$(dirname "$0")/.."
if [ -z "${STUDENT_JAR:-}" ]; then
    for candidate in "$HOME/eclipse-workspace/CS2-Support/student.jar" ../CS2-Support/student.jar; do
        if [ -f "$candidate" ]; then STUDENT_JAR="$candidate"; break; fi
    done
fi
: "${STUDENT_JAR:?Set STUDENT_JAR to the course CS2-Support/student.jar}"
# The course library uses SecurityManager, which Java 24+ no longer supports.
# Honor JAVA_HOME; otherwise prefer the installed Java 11 on macOS.
test_java_home=${JAVA_HOME:-}
if [ -z "$test_java_home" ] && [ -x /usr/libexec/java_home ]; then
    test_java_home=$(/usr/libexec/java_home -v 11 2>/dev/null || true)
fi
test_java=java
test_javac=javac
if [ -n "$test_java_home" ]; then
    test_java="$test_java_home/bin/java"
    test_javac="$test_java_home/bin/javac"
fi
build_dir=$(mktemp -d "${TMPDIR:-/tmp}/dungeon-tests.XXXXXX")
trap 'rm -rf "$build_dir"' EXIT HUP INT TERM
"$test_javac" -encoding UTF-8 -cp "$STUDENT_JAR" -d "$build_dir" src/javaPackage/*.java
"$test_java" -Djava.awt.headless=true -cp "$build_dir:$STUDENT_JAR" org.junit.runner.JUnitCore \
    javaPackage.LinkedChainTest javaPackage.PlaceTest javaPackage.EnemyTest \
    javaPackage.PlayerTest javaPackage.ItemTest \
    javaPackage.DungeonTest javaPackage.GameTest javaPackage.GameWindowTest
