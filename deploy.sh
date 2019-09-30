#! /usr/bin/env sh

lein fig:min && \
    tar cfz deploy.tgz \
        resources/public/index.html \
        resources/public/img/logo.png \
        resources/public/css/style.css \
        resources/public/js/main.js