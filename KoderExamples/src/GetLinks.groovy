/* since this is above the FM tag  it will not be evaluated in FileMaker*/

url = "http://www.geistinteractive.com";

/*----[ above this line is ignored by FileMaker   ---*/


/**
 * returns a list of all links on a web page
 *
 *
 * @FileMakerFunctionName GetLinks
 * @param url
 */

import org.ccil.cowan.tagsoup.Parser
import groovy.xml.StreamingMarkupBuilder
import groovy.xml.XmlUtil



def HTMLtoParse = '';

// some java to get the web page
URL url = new URL(url);
InputStream input = url.openStream();
try {
    StringBuffer sb = new StringBuffer( input.available() );
    Reader r = new InputStreamReader( input, "utf-8" );
    char[] buff = new char[2048];
    int charsRead;
    while( (charsRead=r.read( buff )) != -1 ) {
        sb.append( buff, 0, charsRead );
    }
    HTMLtoParse = sb.toString();
} finally {
    input.close();
}

// parse the links with tag soup and some groovy
def parser = new Parser()
parser.setFeature("http://xml.org/sax/features/namespaces", false);

def slurper = new XmlSlurper(parser)

html = slurper.parseText(HTMLtoParse)

links = html.depthFirst().findAll { it.name() == 'a' }

result = []
for (link in links){
    result.add( link.@href );
}

return result;

/*----[ below this line is ignored by FileMaker ] --*/

/* since this is below the FM marker this will not be evaluated */
print result;
