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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
import org.freaknet.gtrends.api.exceptions.GoogleAuthenticatorException;

/**
 *
 * @author Marco Tizzoni <marco.tizzoni@gmail.com>
 */
public class GoogleAuthenticator {

    private static final String URL_SERVICE_LOGIN_BOX_AUTH = "https://accounts.google.com/ServiceLoginAuth";
    private static final String URL_COOKIE_CHECK = "https://www.google.com/settings/account";
    private static final String RE_GALX = "<input type=\"hidden\"[ \n\t]+name=\"GALX\"[ \n\t]+value=\"([a-zA-Z0-9_-]+)\">";
    private static final String FORM_INPUT_EMAIL = "Email";
    private static final String FORM_INPUT_PASSWD = "Passwd";
    private static final String FORM_INPUT_PERSISTENT_COOKIE = "PersistentCookie";
    private static final String FORM_INPUT_GALX = "GALX";
    private static final String RE_IS_LOGGEDIN = "https://accounts.google.com/[a-zA-Z0-9_-]+/[a-zA-Z0-9_-]+/EditPasswd";
    private static final String URL_GOOGLE = "http://www.google.com";

    private String username = "";
    private String passwd = "";
    private DefaultHttpClient client;
    private boolean isLoggedIn = false;

    public GoogleAuthenticator(String username, String passwd) {
        this.username = username;
        this.passwd = passwd;
        this.client = new DefaultHttpClient();
    }

    public GoogleAuthenticator(String username, String passwd, DefaultHttpClient client) {
        this.username = username;
        this.passwd = passwd;
        this.client = client;
    }

    public boolean authenticate() throws GoogleAuthenticatorException {
        CookieStore cookieStore = new BasicCookieStore();
        HttpContext localContext = new BasicHttpContext();
        localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

        String galx = galx();
        return login(galx);
    }

    public boolean isLoggedIn(){
        return isLoggedIn;
    }

    private String galx() throws GoogleAuthenticatorException {
        String galx = null;
        Pattern pattern = Pattern.compile(RE_GALX, Pattern.CASE_INSENSITIVE);
        HttpGet get = new HttpGet(URL_SERVICE_LOGIN_BOX_AUTH);

        try {
            HttpResponse response = this.client.execute(get);
            String html = GoogleUtils.toString(response.getEntity().getContent());
            Matcher matcher = pattern.matcher(html);
            while (matcher.find()) {
                galx = matcher.group(1);
            }

            if (galx == null) {
                throw new GoogleAuthenticatorException("Cannot parse GALX!");
            }

        } catch (ClientProtocolException ex) {
            throw new GoogleAuthenticatorException(ex);
        } catch (IOException ex) {
            throw new GoogleAuthenticatorException(ex);
        }

        return galx;
    }

    private boolean login(String galx) throws GoogleAuthenticatorException {
        isLoggedIn = false;

        try {
            HttpPost httpPost = new HttpPost(GoogleAuthenticator.URL_SERVICE_LOGIN_BOX_AUTH);
            GoogleUtils.setupHttpRequestDefaults(httpPost);
            List<NameValuePair> formInputs = new ArrayList<NameValuePair>();
            formInputs.add(new BasicNameValuePair(FORM_INPUT_EMAIL, this.username));
            formInputs.add(new BasicNameValuePair(FORM_INPUT_PASSWD, this.passwd));
            formInputs.add(new BasicNameValuePair(FORM_INPUT_PERSISTENT_COOKIE, "yes"));
            formInputs.add(new BasicNameValuePair(FORM_INPUT_GALX, galx));
            httpPost.setEntity(new UrlEncodedFormEntity(formInputs, HTTP.UTF_8));
            httpPost.addHeader("Referrer", URL_SERVICE_LOGIN_BOX_AUTH);

            HttpResponse response = client.execute(httpPost);
            GoogleUtils.toString(response.getEntity().getContent());

            HttpGet httpGet = new HttpGet(new URI(URL_COOKIE_CHECK));
            GoogleUtils.setupHttpRequestDefaults(httpGet);
            String content = GoogleUtils.toString(client.execute(httpGet).getEntity().getContent());

            Pattern p = Pattern.compile(RE_IS_LOGGEDIN);
            Matcher m = p.matcher(content);

            if (m.find()) {
                isLoggedIn = true;
            } else {
                throw new GoogleAuthenticatorException("Failed to login!");
            }

            httpGet = new HttpGet(new URI(URL_GOOGLE));
            GoogleUtils.setupHttpRequestDefaults(httpGet);
            GoogleUtils.toString(client.execute(httpGet).getEntity().getContent());

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
        }

        return isLoggedIn;
    }
}
