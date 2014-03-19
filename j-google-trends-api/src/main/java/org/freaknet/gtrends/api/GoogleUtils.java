package org.freaknet.gtrends.api;

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


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.DataConfiguration;
import org.apache.http.client.methods.HttpRequestBase;

/**
 *
 * @author Marco Tizzoni <marco.tizzoni@gmail.com>
 */
public class GoogleUtils {

    private GoogleUtils() {
    }

    /**
     * Setup the <code>HttpRequestBase</code> r with default headers and HTTP
     * parameters.
     *
     * @param r <code>HttpRequestBase</code> to setup
     */
    public static void setupHttpRequestDefaults(HttpRequestBase r) throws ConfigurationException {
        DataConfiguration config = GoogleConfigurator.getConfiguration();

        r.addHeader("Content-type", config.getString("request.default.content-type"));
        r.addHeader("User-Agent", config.getString("request.default.user-agent"));
        r.addHeader("Accept", config.getString("request.default.accept"));
    }

    /**
     * Eat the <code>InputStream</code> in and return a string.
     *
     * @param in <code>InputStream</code> to read from
     * @return a <code>String</code> representation of the stream
     */
    public static String toString(InputStream in) throws IOException {
        String string;
        StringBuilder outputBuilder = new StringBuilder();
        if (in != null) {
            BufferedReader reader
                    = new BufferedReader(new InputStreamReader(in));
            while (null != (string = reader.readLine())) {
                outputBuilder.append(string).append('\n');
            }
        }
        return outputBuilder.toString();
    }
}
