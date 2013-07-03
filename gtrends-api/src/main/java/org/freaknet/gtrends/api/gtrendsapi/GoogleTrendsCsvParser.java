package org.freaknet.gtrends.api.gtrendsapi;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Marco Tizzoni <marco.tizzoni@gmail.com>
 */
public class GoogleTrendsCsvParser {
    String csv;

    public GoogleTrendsCsvParser(String csv){
        this.csv = csv;
    }

    public String getSection(String section, boolean header){
        String ret = null;

        Pattern startSectionPattern = Pattern.compile("^" + section + ".*$", Pattern.MULTILINE);
        Matcher matcher = startSectionPattern.matcher(csv);
        if (matcher.find()){
            ret = csv.subSequence(matcher.start(), csv.length()).toString();

            int end = ret.length();

            Pattern endSectionPattern = Pattern.compile("\n\n\n", Pattern.MULTILINE);
            matcher = endSectionPattern.matcher(ret);
            if (matcher.find()){
                end = matcher.start();
            }

            ret = ret.subSequence(0, end).toString();

            if (header){
                ret = ret.substring(ret.indexOf('\n') + 1);
            }
        }
        return ret;
    }
}
