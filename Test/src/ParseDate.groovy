/**
 * Simple Natural Date Parsing function
 *
 * Uses natty http://natty.joestelmach.com/
 *
 * @FileMakerFunctionName ParseDate
 * @param dateString
 *
 */



import com.joestelmach.natty.*;
import org.antlr.runtime.*

Parser parser = new Parser();
List groups = parser.parse("the day before next thursday");
for(DateGroup group:groups) {
  List dates = group.getDates();
}

