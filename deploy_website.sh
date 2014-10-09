#!/bin/bash
set -x
cp -R site/output/ .
git status
git add -u
git add apidocs
git add *.html
git add images
git add js
git add css
git add fonts
git commit -m "updated website"
git push origin gh-pages