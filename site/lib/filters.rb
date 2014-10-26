# Remove footer as it is supplied by our layout instead.
class FooterRemoveMarkdownFilter < Nanoc::Filter
  identifier :footer_remove

  def run(content, params={})
    content.gsub(/^\/[^\*][^\r\n]+$/, '')
  end
end

# Enclose named anchors with correct bootstrap markup
class BootstrapHtmlFilter < Nanoc::Filter
  identifier :bootstrap

  def run(content, params={})
    content=content.sub('<p><a name="jumbotron-start"></a></p>', '<div class="jumbotron">')
    content=content.sub('<p><a name="jumbotron-end"></a></p>', '</div>')

    content=content.sub('<p><a name="important-start"></a></p>', '<div class="alert alert-info">')
    content=content.sub('<p><a name="important-end"></a></p>', '</div>')
    content
  end
end

# Links are designed to work for github repository files but for the homepage they are named differently and positioned in the same dir. Thus our .md files becomes local html files instead and relative links to sources become absolute.
class FixLinksHtmlFilter < Nanoc::Filter
  identifier :fixlinks
  def run(content, params={})
    content=content.gsub(/<a\shref="([\w\-\/]*).(m|M)(d|D)">/, '<a href="\\1.html">')

    # TODO: Rewrite into more general replacements
    content=content.gsub(/<a\shref=".*valjogen-processor\/README\.(html|md)">/, '<a href="processor-readme.html">')
    content=content.gsub(/<a\shref=".*valjogen-examples\/README\.(html|md)">/, '<a href="examples-readme.html">')
    content=content.gsub(/<a\shref=".*valjogen-integrationtests\/README\.(html|md)">/, '<a href="integrationtests-readme.html">')
    content=content.gsub(/<a\shref=".*valjogen-annotations\/README\.(html|md)">/, '<a href="annotations-readme.html">')
    content=content.gsub(/"GETSTARTED.(html|md)"/, '"getstarted.html"')
    content=content.gsub(/"CONTRIBUTING.(html|md)"/, '"contributing.html"')
    content=content.gsub(/"INDEX.(html|md)"/, '"index.html"')
    content=content.gsub(/"INDEX.(html|md)"/, '"index.html"')
    content=content.gsub(/"LICENSE.(html|md)"/, '"license.html"')
    content=content.gsub(/"DOWNLOADS.(html|md)"/, '"downloads.html"')
    content=content.gsub(/"NEWS.(html|md)"/, '"news.html"')

    content=content.gsub('<a href="src/main/java/com/fortyoneconcepts/valjogen/examples">', '<a href="http://github.com/41concepts/VALJOGen/tree/master/valjogen-examples/src/main/java/com/fortyoneconcepts/valjogen/examples">')
    content=content.gsub('<a href="valjogen-annotations/src/main/java/com/fortyoneconcepts/valjogen/annotations">', '<a href="http://github.com/41concepts/VALJOGen/tree/master/valjogen-annotations/src/main/java/com/fortyoneconcepts/valjogen/annotations">')
    content
  end
end

# Inserts links to javadoc for annotations in html
class InsertJavaDocApiLinksHtmlFilter < Nanoc::Filter
  identifier :insert_javadocapi_links

  def run(content, params={})
    content=content.gsub(/(@VALJO(\w+)(\([^\)]+\))?)(?!<\/a>)/, '<a href="apidocs/com/fortyoneconcepts/valjogen/annotations/VALJO\\2.html" title="See JavaDoc">\\1</a>')
    content
  end
end
