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

import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.DataConfiguration;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.freaknet.gtrends.api.exceptions.GoogleTrendsRequestException;

/**
 *
 * @author Marco Tizzoni <marco.tizzoni@gmail.com>
 */
public class GoogleTrendsRequest {
    private String query;
    private URIBuilder builder;

    /**
     *
     * @param q
     */
    public GoogleTrendsRequest(String q) throws ConfigurationException, URISyntaxException {
        this.query = q;
        DataConfiguration config = GoogleConfigurator.getConfiguration();
        String[] params = config.getStringArray("google.trends.params");
        this.builder = new URIBuilder(config.getString("google.trends.url"));
        builder.setParameter("q", this.query);
        
        // Set defaults
        for (int i = 0; i < params.length; i++) {
            builder.setParameter(params[i], config.getString("google.trends.param." + params[i]));
        }
    }

    /**
     * Build the
     * <code>HttpRequestBase</code> with the provided parameters.
     *
     * @return the built request
     */
    public HttpRequestBase build() throws GoogleTrendsRequestException {
        return build(new BasicNameValuePair[0]);
    }

    /**
     * Build the
     * <code>HttpRequestBase</code> with the provided parameters.
     *
     * @param params Parameters request to set
     * @return the built request
     */
    public HttpRequestBase build(BasicNameValuePair[] params) throws GoogleTrendsRequestException {
        HttpRequestBase request = null;
        try {
            for (int i = 0; i < params.length; i++) {
                BasicNameValuePair p = params[i];
                builder.setParameter(p.getName(), p.getValue());
            }
            request = new HttpGet(builder.build());
            GoogleUtils.setupHttpRequestDefaults(request);
        } catch (URISyntaxException ex) {
            throw new GoogleTrendsRequestException(ex);
        } catch (ConfigurationException ex) {
            throw new GoogleTrendsRequestException(ex);
        }

        return request;
    }

    /**
     * Set a parameter value.
     *
     * @param name
     * @param value
     */
    public void setParam(String name, String value) {
        builder.setParameter(name, value);
    }

    /**
     * Get a parameter value.
     *
     * @param name
     * @return value
     */
    public String getParam(String name) {
        String value = null;
        Iterator<NameValuePair> i = builder.getQueryParams().iterator();
        while (i.hasNext()) {
            NameValuePair nameValuePair = i.next();
            if (nameValuePair.getName().equals(name)) {
                value = nameValuePair.getValue();
                break;
            }
        }
        return value;
    }

    /**
     * Get the request parameters.
     * @return the request parameters
     */
    public List<NameValuePair> getQueryParams() {
        return builder.getQueryParams();
    }
    
    /**
     * Set the request parameters.
     * @param params the request parameters
     */
    public void setQueryParams(List<NameValuePair> params) {
        Iterator<NameValuePair> i = params.iterator();
        while (i.hasNext()) {
            NameValuePair p = i.next();
            builder.setParameter(p.getName(), p.getValue());
        }
    }
}
