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
package org.freaknet.gtrends.api.exceptions;

/**
 *
 * @author Marco Tizzoni <marco.tizzoni@gmail.com>
 */
public class GoogleTrendsClientException extends Exception {

  /**
   * Creates a new instance of <code>GoogleTrendsClientException</code> without
   * detail message.
   */
  public GoogleTrendsClientException() {
  }

  /**
   * Constructs an instance of <code>GoogleTrendsClientException</code> with the
   * specified detail message.
   *
   * @param msg the detail message.
   */
  public GoogleTrendsClientException(String msg) {
    super(msg);
  }

  /**
   * Constructs an instance of <code>GoogleTrendsClientException</code> with the
   * specified exception.
   *
   * @param e the detail message.
   */
  public GoogleTrendsClientException(Exception e) {
    super(e);
  }
}
