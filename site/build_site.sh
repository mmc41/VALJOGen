#!/usr/bin/env ruby
currentPath = File.dirname(File.expand_path(__FILE__))

puts `nanoc`
puts `cd #{currentPath} && rsync --ignore-existing --recursive ../valjogen-processor/target/apidocs/ output/apidocs`