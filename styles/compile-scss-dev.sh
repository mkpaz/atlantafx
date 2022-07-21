#!/usr/bin/env sh

# This scipt compiles SCSS directly to the sampler classpath for hot reload (CSSFX).

PATH="../node:$(../node/npm bin):$PATH"
NODE_ENV=dev grunt --verbose --gruntfile=Gruntfile.js
