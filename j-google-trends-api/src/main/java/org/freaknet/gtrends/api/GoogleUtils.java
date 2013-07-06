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
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.http.client.methods.HttpRequestBase;

/**
 *
 * @author Marco Tizzoni <marco.tizzoni@gmail.com>
 */
public class GoogleUtils {

    private static final String HEADER_DEFAULT_CONTENT_TYPE = "application/x-www-form-urlencoded";
    private static final String HEADER_DEFAULT_USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21";
    private static final String HEADER_DEFAULT_ACCEPT = "text/plain";

    /**
     * Setup the
     * <code>HttpRequestBase</code> r with default headers and HTTP parameters.
     * @param r <code>HttpRequestBase</code> to setup
     */
    public static void setupHttpRequestDefaults(HttpRequestBase r) {
        r.addHeader("Content-type", HEADER_DEFAULT_CONTENT_TYPE);
        r.addHeader("User-Agent", HEADER_DEFAULT_USER_AGENT);
        r.addHeader("Accept", HEADER_DEFAULT_ACCEPT);
    }

    /**
     * Eat the <code>InputStream</code> in and return a string.
     * @param in <code>InputStream</code> to read from
     * @return a <code>String</code> representation of the stream
     */
    public static String toString(InputStream in) throws IOException {
        String string;
        StringBuilder outputBuilder = new StringBuilder();
        if (in != null) {
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(in));
            while (null != (string = reader.readLine())) {
                outputBuilder.append(string).append('\n');
            }
        }
        return outputBuilder.toString();
    }
}
