j-google-trends-api
===================
Java based implementation of Unofficial Google Trends API

Example
=======
    import org.apache.http.HttpHost;
    import org.apache.http.auth.AuthScope;
    import org.apache.http.auth.Credentials;
    import org.apache.http.auth.NTCredentials;
    import org.apache.http.conn.params.ConnRoutePNames;
    import org.apache.http.impl.client.DefaultHttpClient;
    import org.freaknet.gtrends.api.gtrendsapi.GoogleAuthenticator;
    import org.freaknet.gtrends.api.gtrendsapi.GoogleTrendsClient;
    import org.freaknet.gtrends.api.gtrendsapi.GoogleTrendsCsvParser;
    import org.freaknet.gtrends.api.gtrendsapi.GoogleTrendsRequest;
    import org.freaknet.gtrends.api.gtrendsapi.exceptions.GoogleTrendsClientException;
    
    public class App {
    
        public static void main(String[] args) throws GoogleTrendsClientException {
            String u = "myuser@gmail.com";
            String p = "mypasswd";
            
            /* OPTIONAL: setup a proxy with NTLM authentication */
            HttpHost proxy = new HttpHost("proxy.mydomain.com", 8080, "http");
            Credentials credentials = new NTCredentials("myLogin", "myPasswd", "", "DOMAIN");
            DefaultHttpClient httpClient = new DefaultHttpClient();
            httpClient.getCredentialsProvider().setCredentials(new AuthScope(proxy.getHostName(), proxy.getPort()), credentials);
            httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
    
            /* Creates a new authenticator */
            GoogleAuthenticator authenticator = new GoogleAuthenticator(u, p, httpClient);
            
            /* Creates a new Google Trends Client */
            GoogleTrendsClient client = new GoogleTrendsClient(authenticator, httpClient);
            GoogleTrendsRequest request = new GoogleTrendsRequest("bana");
            
            /* Here the default request params can be modified with getter/setter methods */
            String content = client.execute(request);
             
            /* The default request downloads a CSV available in content */
            GoogleTrendsCsvParser csvParser = new GoogleTrendsCsvParser(content);
            /* Get a specific section of the CSV */
            String section = csvParser.getSection("Top searches for", true);
            System.out.println(section);
        }
    }

CREDITS
=======
Based on the original python version available here: https://github.com/suryasev/unofficial-google-trends-api

LICENSE
=======
j-google-trends-api
Java based implementation of Unofficial Google Trends API
Copyright (C) 2013  Marco Tizzoni

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
