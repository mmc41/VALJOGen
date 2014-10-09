This nanoc3 project is used to generate the static website for this project. Text is based on the *.md markdown files that github recoginizes. As nanoc wants content files to be in the content folder
relative symbolic links are used to put them there under new names to avoid clashes (*). For layout and design the bootstrap FW is used. The lib directory contains custom code like a preprocessor to 
pull in examples and create a example summary file on demand + custom filters to fix links and enhance the layout.

The names of the markdown source files are dupliced across folders and named as suited for github according to its guidelines. The symbolic links names the files in a way that better fit the needs 
of nanoc and the website. For instance the main README.md is linked to INDEX.md as this is the source for the front page. In addition the ouput html files and link references are all converted to lowercase. 

To generate the website type "./build_site.sh" inside a command prompt inside this directory. Then switch to the gh-pages branch and execute the ./deploy_website.sh script there.

To test the generated website locally before deploying type "nanoc view" in a command prompt and open a browser at http://localhost:3000

To install the necessary tools install ruby 1.9 or later, rubygems and type:
sudo git install nanoc
sudo gem install adsf
sudo gem install kramdown
sudo gem install coderay

See install details at "http://nanoc.ws/"

(*) Future consideration: In case the symbolic links give problems we could use a preprocessor instead to pull in the files and create copies in memory.

/ Morten M. Christensen, [41concepts](http://www.41concepts.com)