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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
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
  public GoogleTrendsCsvParser(String csv) {
    this.csv = csv;
  }

  /**
   *
   * @param section Section of the CSV to retrieve
   * @param header If the section has a header. If <code>true</code> the first
   * line will be skipped.
   * @return content The content of the section
   */
  public String getSectionAsString(String section, boolean header) {
    String ret = null;

    Pattern startSectionPattern = Pattern.compile("^" + section + ".*$", Pattern.MULTILINE);
    Matcher matcher = startSectionPattern.matcher(csv);
    if (matcher.find()) {
      ret = csv.subSequence(matcher.start(), csv.length()).toString();

      int end = ret.length();

      Pattern endSectionPattern = Pattern.compile("\n\n\n", Pattern.MULTILINE);
      matcher = endSectionPattern.matcher(ret);
      if (matcher.find()) {
        end = matcher.start();
      }

      ret = ret.subSequence(0, end).toString().substring(ret.indexOf('\n') + 1);

      if (header) {
        ret = ret.substring(ret.indexOf('\n') + 1);
      }
    }

    return ret;
  }

  /**
   * Retrieve the CSV section as list of <code>String[]</code>.
   * @param section Section of the CSV to retrieve
   * @param header If the section has a header. If <code>true</code> the first
   * line will be skipped.
   * @param fieldSep Field separator for each line
   * @return An <code>ArrayList</code> of <code>String[]</code> where each element in
   * the <code>ArrayList</code> is a row and each <code>String</code> is a column
   * @throws IOException
   */
  public ArrayList<String[]> getSectionAsStringArrayList(String section, boolean header, String fieldSep) throws IOException {
    ArrayList<String[]> ret = new ArrayList<String[]>();
    BufferedReader r = new BufferedReader(new StringReader(getSectionAsString(section, header)));

    String line;
    while ((line = r.readLine()) != null) {
      ret.add(line.split(fieldSep));
    }

    return ret;
  }

  /**
   * Retrieve the CSV section as list of <code>String</code>.
   * @param section Section of the CSV to retrieve
   * @param header If the section has a header. If <code>true</code> the first
   * line will be skipped.
   * @param fieldSep Field separator for each line
   * @return An <code>ArrayList</code> of <code>String</code> where each <code>String</code>
   * element in the <code>ArrayList</code> is a row
   * @throws IOException
   */
  public ArrayList<String> getSectionAsStringList(String section, boolean header, String fieldSep) throws IOException {
    ArrayList<String> ret = new ArrayList<String>();
    BufferedReader r = new BufferedReader(new StringReader(getSectionAsString(section, header)));


    String line;
    while ((line = r.readLine()) != null) {
      ret.add(line);
    }

    return ret;
  }
}
