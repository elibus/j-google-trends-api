package org.freaknet.gtrends.api.gtrendsapi;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.freaknet.gtrends.api.gtrendsapi.exceptions.GoogleAuthenticatorException;
import org.freaknet.gtrends.api.gtrendsapi.exceptions.GoogleTrendsClientException;

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