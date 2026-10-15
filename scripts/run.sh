#!/bin/sh
set -eu
cd "$(dirname "$0")/.."
build_dir=$(mktemp -d "${TMPDIR:-/tmp}/dungeon-game.XXXXXX")
trap 'rm -rf "$build_dir"' EXIT HUP INT TERM
javac -encoding UTF-8 -d "$build_dir" src/javaPackage/Game.java src/javaPackage/GameWindow.java \
    src/javaPackage/Dungeon.java src/javaPackage/Place.java src/javaPackage/Enemy.java \
    src/javaPackage/Player.java src/javaPackage/Item.java src/javaPackage/LinkedChain.java
java -cp "$build_dir" javaPackage.Game "$@"
