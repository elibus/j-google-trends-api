/**
 * Copyright (C) 2012 Marco Tizzoni <marco.tizzoni@gmail.com>
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

import org.freaknet.gtrends.api.GoogleAuthenticator;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.freaknet.gtrends.api.exceptions.GoogleAuthenticatorException;
import org.freaknet.gtrends.api.exceptions.GoogleTrendsClientException;

/**
 *
 * @author Marco Tizzoni <marco.tizzoni@gmail.com>
 */
public class GoogleTrendsClient {

    GoogleAuthenticator authenticator;
    private DefaultHttpClient client;

    public GoogleTrendsClient(GoogleAuthenticator authenticator) {
        this.authenticator = authenticator;
        this.client = new DefaultHttpClient();
    }

    public GoogleTrendsClient(GoogleAuthenticator authenticator, DefaultHttpClient client) {
        this.authenticator = authenticator;
        this.client = client;
    }

    public String execute(GoogleTrendsRequest request) throws GoogleTrendsClientException {
        String ret = null;
        try {
            if (!authenticator.isLoggedIn()) {
                authenticator.authenticate();
            }
            HttpRequestBase httpRequest = request.build();
            HttpResponse response = client.execute(httpRequest);
            ret = GoogleUtils.toString(response.getEntity().getContent());

            HttpGet get = new HttpGet("http://www.google.com");
            get.addHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21");
            get.addHeader("Accept", "text/plain");
            get.addHeader("Referrer", "https://www.google.com/accounts/ServiceLoginBoxAuth");
            client.execute(get);

            Pattern p = Pattern.compile(".*An error has been detected.*", Pattern.CASE_INSENSITIVE);
            Matcher matcher = p.matcher(ret);
            if (matcher.find()) {
                throw new GoogleTrendsClientException("The response body does not look like a CSV: " + ret);
            }
        } catch (GoogleAuthenticatorException ex) {
             throw new GoogleTrendsClientException(ex);
        } catch (ClientProtocolException ex) {
            throw new GoogleTrendsClientException(ex);
        } catch (IOException ex) {
            throw new GoogleTrendsClientException(ex);
        }

        return ret;
    }
}