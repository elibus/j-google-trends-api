package org.freaknet.gtrends.api.gtrendsapi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

/**
 *
 * @author elibus
 */
public class App {

  /**
   * Writes the content of the input stream to a <code>String<code>.
   */
  private static String toString(InputStream inputStream) throws IOException {
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

  public static void main(String[] args) throws IOException, URISyntaxException {
    //URL url = new URL("https://www.google.com/accounts/ClientLogin");
    CookieStore cookieStore = new BasicCookieStore();
    HttpContext localContext = new BasicHttpContext();
    localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

    HttpClient client = new DefaultHttpClient();
    URIBuilder builder = new URIBuilder();
    builder.setScheme("https").setHost("accounts.google.com").setPath("/ServiceLoginAuth");
    URI uri = builder.build();

    HttpPost httpPost = new HttpPost(uri);
    List<NameValuePair> nvps = new ArrayList<NameValuePair>();
    nvps.add(new BasicNameValuePair("Email", "xxxxxx@gmail.com"));
    nvps.add(new BasicNameValuePair("Passwd", "xxxxyyyyzzzz"));
    nvps.add(new BasicNameValuePair("source", "gtrends-api"));
    nvps.add(new BasicNameValuePair("accountType", "GOOGLE"));
    nvps.add(new BasicNameValuePair("service", "trends"));

    httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
    httpPost.addHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21");
    httpPost.addHeader("Accept","text/plain");
    HttpResponse response = client.execute(httpPost);
    System.out.println(response.getStatusLine());
    System.out.println("Initial set of cookies:");
    List<Cookie> cookies = cookieStore.getCookies();
    if (cookies.isEmpty()) {
        System.out.println("None");
    } else {
        for (int i = 0; i < cookies.size(); i++) {
            System.out.println("- " + cookies.get(i).toString());
        }
    }

    String content = toString(response.getEntity().getContent());
    String[] split = content.split("<input type=\"hidden\" name=\"GALX\" value=\"");
    System.out.println("A: " + split[0]);

//    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//    urlConnection.setRequestMethod("POST");
//    urlConnection.setDoInput(true);
//    urlConnection.setDoOutput(true);
//    urlConnection.setUseCaches(false);
//    urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

// Form the POST parameters
//    StringBuilder content = new StringBuilder();
//    content.append("Email=").append(URLEncoder.encode("elibus@gmail.com", "UTF-8"));
//    content.append("&Passwd=").append(URLEncoder.encode("4seJ_mpmLfi5aFcBi_JB_4qyz", "UTF-8"));
//    content.append("&source=").append(URLEncoder.encode("gtrends-api", "UTF-8"));
//    content.append("&accountType=").append(URLEncoder.encode("GOOGLE", "UTF-8"));
//    content.append("&service=").append(URLEncoder.encode("trends", "UTF-8"));
//
//    OutputStream outputStream = urlConnection.getOutputStream();
//    outputStream.write(content.toString().getBytes("UTF-8"));
//    outputStream.close();
//
//// Retrieve the output
//    int responseCode = urlConnection.getResponseCode();
//    InputStream inputStream;
//    if (responseCode == HttpURLConnection.HTTP_OK) {
//      inputStream = urlConnection.getInputStream();
//    } else {
//      inputStream = urlConnection.getErrorStream();
//    }

    //String postOutput = toString(response.getEntity().getContent());
    String postOutput = null;
    StringTokenizer tokenizer = new StringTokenizer(postOutput, "=\n ");
    String token = null;

    while (tokenizer.hasMoreElements()) {
      if (tokenizer.nextToken().equals("Auth")) {
        if (tokenizer.hasMoreElements()) {
          token = tokenizer.nextToken();
        }
        break;
      }
    }
    if (token == null) {
      System.out.println("Authentication error. Response from server:\n" + postOutput);
      System.exit(1);
    }
//
//    HttpURLConnection connection = (HttpURLConnection) (new URL("http://www.google.com/trends/trendsReport?hl=en-US&q=jobs%20-%22steve%20jobs%22&cmpt=q&content=1&export=1")).openConnection();
//
//    connection.setDoInput(true);
//    connection.setDoOutput(true);
//
//    connection.setRequestMethod("GET");
    //connection.setRequestProperty("Content-Type", "application/atom+xml");
    //connection.setRequestProperty("Authorization", "GoogleLogin auth=" + token);
    HttpGet method = new HttpGet("http://www.google.com/trends/trendsReport?hl=en-US&q=jobs%20-%22steve%20jobs%22&cmpt=q&content=1&export=1");
    method.addHeader("Authorization", "GoogleLogin auth=" + token);

    response = client.execute(method, localContext);

    BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
    String line = "";
    while ((line = rd.readLine()) != null) {
      System.out.println(line);
    }

//    // Post the data item
//    outputStream = connection.getOutputStream();
//    //outputStream.write(DATA_ITEM.getBytes());
//    outputStream.close();
//
//    // Retrieve the output
//    responseCode = connection.getResponseCode();
//
//    if (responseCode == HttpURLConnection.HTTP_NOT_AUTHORITATIVE) {
//      System.out.println("SIZE: " + responseCode);
//      inputStream = connection.getInputStream();
//    } else {
//      inputStream = connection.getErrorStream();
//    }
//
//    // write the output to the console
//    System.out.println(toString(inputStream));
  }
}
