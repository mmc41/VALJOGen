#!/usr/bin/env ruby
currentPath = File.dirname(File.expand_path(__FILE__))

puts `nanoc`
puts `cd #{currentPath} && rsync --recursive ../valjogen-processor/target/apidocs/ output/apidocs`
""
puts "Website files build. To Deploy make sure all files in master branch are checked in, then do a 'git checkout gh-pages', then type './deploy_website.sh' and switch back to master branch."