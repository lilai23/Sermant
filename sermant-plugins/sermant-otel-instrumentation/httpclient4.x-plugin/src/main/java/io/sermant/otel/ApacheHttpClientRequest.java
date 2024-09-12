/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.sermant.otel;

import static java.util.logging.Level.FINE;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.methods.HttpUriRequest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

public final class ApacheHttpClientRequest {
    private static final Logger logger = Logger.getLogger(ApacheHttpClientRequest.class.getName());

    private final URI uri;

    private final HttpRequest delegate;

    public ApacheHttpClientRequest(HttpHost httpHost, HttpRequest httpRequest) {
        URI calculatedUri = getUri(httpRequest);
        if (calculatedUri != null && httpHost != null) {
            uri = getCalculatedUri(httpHost, calculatedUri);
        } else {
            uri = calculatedUri;
        }
        delegate = httpRequest;
    }

    public ApacheHttpClientRequest(HttpUriRequest httpRequest) {
        uri = httpRequest.getURI();
        delegate = httpRequest;
    }

    public List<String> getHeader(String name) {
        return headersToList(delegate.getHeaders(name));
    }

    // minimize memory overhead by not using streams
    static List<String> headersToList(Header[] headers) {
        if (headers == null || headers.length == 0) {
            return Collections.emptyList();
        }
        List<String> headersList = new ArrayList<>(headers.length);
        for (int i = 0; i < headers.length; ++i) {
            headersList.add(headers[i].getValue());
        }
        return headersList;
    }

    public void setHeader(String name, String value) {
        delegate.setHeader(name, value);
    }

    public String getMethod() {
        return delegate.getRequestLine().getMethod();
    }

    public String getUrl() {
        return uri != null ? uri.toString() : null;
    }

    String getProtocolName() {
        return delegate.getProtocolVersion().getProtocol();
    }

    String getProtocolVersion() {
        ProtocolVersion protocolVersion = delegate.getProtocolVersion();
        if (protocolVersion.getMinor() == 0) {
            return Integer.toString(protocolVersion.getMajor());
        }
        return protocolVersion.getMajor() + "." + protocolVersion.getMinor();
    }

    public String getServerAddress() {
        return uri == null ? null : uri.getHost();
    }

    public Integer getServerPort() {
        return uri == null ? null : uri.getPort();
    }

    private static URI getUri(HttpRequest httpRequest) {
        try {
            // this can be relative or absolute
            return new URI(httpRequest.getRequestLine().getUri());
        } catch (URISyntaxException e) {
            logger.log(FINE, e.getMessage(), e);
            return null;
        }
    }

    private static URI getCalculatedUri(HttpHost httpHost, URI uri) {
        try {
            return new URI(
                    httpHost.getSchemeName(),
                    null,
                    httpHost.getHostName(),
                    httpHost.getPort(),
                    uri.getPath(),
                    uri.getQuery(),
                    uri.getFragment());
        } catch (URISyntaxException e) {
            logger.log(FINE, e.getMessage(), e);
            return null;
        }
    }
}
