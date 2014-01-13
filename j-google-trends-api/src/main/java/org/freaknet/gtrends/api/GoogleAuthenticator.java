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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.DataConfiguration;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.freaknet.gtrends.api.exceptions.GoogleAuthenticatorException;

/**
 * Provides a simple way to authenticate with username/password to Google.
 *
 * @author Marco Tizzoni <marco.tizzoni@gmail.com>
 */
public class GoogleAuthenticator {
    private String username = "";
    private String passwd = "";
    private DefaultHttpClient client;
    private boolean isLoggedIn = false;

    /**
     * Provides authentication for Google services.
     *
     * @param username Google email in the form <code>user@google.com</code>
     * @param passwd Google password
     * @param client <code>DefaultHttpClient</code> to use for the connection
     */
    public GoogleAuthenticator(String username, String passwd, DefaultHttpClient client) {
        this.username = username;
        this.passwd = passwd;
        this.client = client;
    }

    /**
     * Starts the authentication process.
     *
     * @return <code>true</code> if authentication was successful
     * @throws GoogleAuthenticatorException
     */
    public boolean authenticate() throws GoogleAuthenticatorException {
        CookieStore cookieStore = new BasicCookieStore();
        HttpContext localContext = new BasicHttpContext();
        localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

        String galx = galx();
        return login(galx);
    }

    /**
     * Checks whether
     * <code>authenticate()</code> was called successfully.
     *
     * @return <code>true</code> if logged in.
     */
    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    /**
     * Parse the login page for the GALX id.
     *
     * @return GALX id
     * @throws GoogleAuthenticatorException
     */
    private String galx() throws GoogleAuthenticatorException {
        String galx = null;
        try {
            DataConfiguration config = GoogleConfigurator.getConfiguration();

            Pattern pattern = Pattern.compile(config.getString("google.auth.reGalx"), Pattern.CASE_INSENSITIVE);
            HttpGet get = new HttpGet(config.getString("google.auth.loginUrl"));

            HttpResponse response = this.client.execute(get);
            String html = GoogleUtils.toString(response.getEntity().getContent());
            Matcher matcher = pattern.matcher(html);
            if (matcher.find()) {
                galx = matcher.group(1);
            }

            if (galx == null) {
                throw new GoogleAuthenticatorException("Cannot parse GALX!");
            }
        } catch (ConfigurationException ex) {
            throw new GoogleAuthenticatorException(ex);
        } catch (ClientProtocolException ex) {
            throw new GoogleAuthenticatorException(ex);
        } catch (IOException ex) {
            throw new GoogleAuthenticatorException(ex);
        }

        return galx;
    }

    /**
     * Login in Google.
     *
     * @param galx The GALX id
     * @return <code>true</code> if login was successful
     * @throws GoogleAuthenticatorException
     */
    private boolean login(String galx) throws GoogleAuthenticatorException {
        isLoggedIn = false;

        try {
            DataConfiguration config = GoogleConfigurator.getConfiguration();
            
            HttpPost httpPost = new HttpPost(config.getString("google.auth.loginUrl"));
            GoogleUtils.setupHttpRequestDefaults(httpPost);
            List<NameValuePair> formInputs = new ArrayList<NameValuePair>();
            formInputs.add(new BasicNameValuePair(config.getString("google.auth.input.email"), this.username));
            formInputs.add(new BasicNameValuePair(config.getString("google.auth.input.passwd"), this.passwd));
            formInputs.add(new BasicNameValuePair(config.getString("google.auth.input.persistentCookie"), "yes"));
            formInputs.add(new BasicNameValuePair(config.getString("google.auth.input.galx"), galx));
            httpPost.setEntity(new UrlEncodedFormEntity(formInputs, HTTP.UTF_8));
            httpPost.addHeader("Referrer", config.getString("google.auth.loginUrl"));

            HttpResponse response = client.execute(httpPost);
            GoogleUtils.toString(response.getEntity().getContent());

            HttpGet httpGet = new HttpGet(new URI(config.getString("google.auth.cookieCheckUrl")));
            GoogleUtils.setupHttpRequestDefaults(httpGet);
            HttpResponse httpResponse = client.execute(httpGet);
            HttpEntity entity = httpResponse.getEntity();
            
//            StatusLine statusLine = httpResponse.getStatusLine();
//            HeaderIterator headerIterator = httpResponse.headerIterator();
            
//            System.out.println("***** STATUS LINE");
//            System.out.println(statusLine);
//            System.out.println("***** HEADER");
//            
//            while (headerIterator.hasNext()) {
//                Header next = (Header) headerIterator.next();
//                System.out.println(next.getName() + next.getValue());
//            }
            
            String content = GoogleUtils.toString(entity.getContent());
//            System.out.println("***** CONTENT");
//            System.out.println(content);

            Pattern p = Pattern.compile(config.getString("google.auth.reIsLoggedIn"));
            Matcher m = p.matcher(content);

            if (m.find()) {
                isLoggedIn = true;
            } else {
                throw new GoogleAuthenticatorException("Failed to login!");
            }

            httpGet = new HttpGet(new URI(config.getString("google.auth.googleUrl")));
            GoogleUtils.setupHttpRequestDefaults(httpGet);
            EntityUtils.consume(client.execute(httpGet).getEntity());

        } catch (UnsupportedEncodingException ex) {
            throw new GoogleAuthenticatorException(ex);
        } catch (ClientProtocolException ex) {
            throw new GoogleAuthenticatorException(ex);
        } catch (IOException ex) {
            throw new GoogleAuthenticatorException(ex);
        } catch (URISyntaxException ex) {
            throw new GoogleAuthenticatorException(ex);
        } catch (GoogleAuthenticatorException ex) {
            throw new GoogleAuthenticatorException(ex);
        } catch (ConfigurationException ex) {
            throw new GoogleAuthenticatorException(ex);
        }

        return isLoggedIn;
    }
}
