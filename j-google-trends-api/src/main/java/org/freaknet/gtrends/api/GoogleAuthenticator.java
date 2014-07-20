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

/**
 * Copyright (C) 2013 Marco Tizzoni <marco.tizzoni@gmail.com>
 *
 * This file is part of j-google-trends-api
 *
 * j-google-trends-api is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * j-google-trends-api is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * j-google-trends-api. If not, see <http://www.gnu.org/licenses/>.
 */
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.DataConfiguration;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.freaknet.gtrends.api.exceptions.GoogleAuthenticatorException;

/**
 * Provides a simple way to authenticate with _username/password to Google.
 *
 * @author Marco Tizzoni <marco.tizzoni@gmail.com>
 */
public class GoogleAuthenticator {

  private String _username = "";
  private String _passwd = "";
  private final HttpClient _httpClient;
  private boolean _isLoggedIn = false;

  /**
   * Provides authentication for Google services.
   *
   * @param username Google email in the form <code>user@google.com</code>
   * @param passwd Google password
   * @param httpClient <code>DefaultHttpClient</code> to use for the connection
   */
  public GoogleAuthenticator(String username, String passwd, HttpClient httpClient) {
    _username = username;
    _passwd = passwd;
    _httpClient = httpClient;
  }

  /**
   * Starts the authentication process.
   *
   * @return <code>true</code> if authentication was successful
   * @throws GoogleAuthenticatorException
   */
  public boolean authenticate() throws GoogleAuthenticatorException {
    String galx = galx();
    return login(galx);
  }

  /**
   * Checks whether <code>authenticate()</code> was called successfully.
   *
   * @return <code>true</code> if logged in.
   */
  public boolean isLoggedIn() {
    return _isLoggedIn;
  }

  /**
   * Parse the login page for the GALX id.
   *
   * @return GALX id
   * @throws GoogleAuthenticatorException
   */
  private String galx() throws GoogleAuthenticatorException {
    String galx = null;
    HttpGet get;
    try {
      DataConfiguration config = GoogleConfigurator.getConfiguration();

      Pattern pattern = Pattern.compile(config.getString("google.auth.reGalx"), Pattern.CASE_INSENSITIVE);
      get = new HttpGet(config.getString("google.auth.loginUrl"));

      HttpResponse response = _httpClient.execute(get);
      String html = GoogleUtils.toString(response.getEntity().getContent());
      get.releaseConnection();
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
    
    //printCookies();

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
    _isLoggedIn = false;

    try {
      DataConfiguration config = GoogleConfigurator.getConfiguration();

      HttpPost httpPost = new HttpPost(config.getString("google.auth.loginAuthenticate"));
      GoogleUtils.setupHttpRequestDefaults(httpPost);
      httpPost.setEntity(new UrlEncodedFormEntity(setupFormInputs(config, galx), HTTP.UTF_8));
      HttpResponse response = _httpClient.execute(httpPost);
      GoogleUtils.toString(response.getEntity().getContent());
      httpPost.releaseConnection();
    } catch (UnsupportedEncodingException ex) {
      throw new GoogleAuthenticatorException(ex);
    } catch (ClientProtocolException ex) {
      throw new GoogleAuthenticatorException(ex);
    } catch (IOException ex) {
      throw new GoogleAuthenticatorException(ex);
    } catch (ConfigurationException ex) {
      throw new GoogleAuthenticatorException(ex);
    }
    
    _isLoggedIn = true;
    return _isLoggedIn;
  }

  /**
   *
   * @param config
   * @param galx
   * @return
   */
  private List<NameValuePair> setupFormInputs(DataConfiguration config, String galx) {
    List<NameValuePair> formInputs = new ArrayList<NameValuePair>();
    formInputs.add(new BasicNameValuePair(config.getString("google.auth.input.email"), _username));
    formInputs.add(new BasicNameValuePair(config.getString("google.auth.input.passwd"), _passwd));

    formInputs.add(new BasicNameValuePair(config.getString("google.auth.input.galx"), galx));
    
    return formInputs;
  }
}
