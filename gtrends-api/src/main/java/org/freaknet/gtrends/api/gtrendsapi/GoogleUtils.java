package org.freaknet.gtrends.api.gtrendsapi;

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
    private static final String HEADER_DEFAULT_PERSISTENT_COOKIE = "yes";

    public static void setupHttpRequestDefaults(HttpRequestBase r) {
        r.addHeader("Content-type", HEADER_DEFAULT_CONTENT_TYPE);
        r.addHeader("User-Agent", HEADER_DEFAULT_USER_AGENT);
        r.addHeader("Accept", HEADER_DEFAULT_ACCEPT);
    }
    
    public static String toString(InputStream inputStream) throws IOException {
        String string;
        StringBuilder outputBuilder = new StringBuilder();
        if (inputStream != null) {
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(inputStream));
            while (null != (string = reader.readLine())) {
                outputBuilder.append(string).append('\n');
            }
        }
        return outputBuilder.toString();
    }
}
