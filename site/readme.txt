This nanoc project is used to generate the static website for this projectl. Text is based on the *.md markdown files that github recoginizes. As nanoc recquires all content files to be in the content folder
realtive symbolic links are used to put them there under new names to avoid clashes. For layout bootstrap is used. Note cutsom filters are used to fix links and enhance the layout.

To generate the website type "./build_site.sh" inside a command prompt inside this directory

To test the website locallt type "nanoc view" in a command prompt and open a browser at http://localhost:3000

To install the necessary tools install ruby 1.9 or later, rubygems and type:
sudo git install nanoc
sudo gem install adsf
sudo gem install kramdown
sudo gem install coderay

See install details at "http://nanoc.ws/"