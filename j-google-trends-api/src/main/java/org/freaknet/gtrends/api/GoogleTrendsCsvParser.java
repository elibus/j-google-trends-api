/**
 * Copyright (C) 2013 Marco Tizzoni <marco.tizzoni@gmail.com>
 *
 * This file is part of j-google-trends-api
 *
 *     j-google-trends-api is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     j-google-trends-api is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with j-google-trends-api.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.freaknet.gtrends.api;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parse the CSV as provided by the CSV Download functionality in Google Trends.
 * @author Marco Tizzoni <marco.tizzoni@gmail.com>
 */
public class GoogleTrendsCsvParser {
    private String csv;

    /**
     * 
     * @param csv 
     */
    public GoogleTrendsCsvParser(String csv){
        this.csv = csv;
    }

    /**
     * 
     * @param section
     * @param header If the section has a header. If <code>true</code> the first
     * line will be skipped.
     * @return content The content of the section
     */
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
