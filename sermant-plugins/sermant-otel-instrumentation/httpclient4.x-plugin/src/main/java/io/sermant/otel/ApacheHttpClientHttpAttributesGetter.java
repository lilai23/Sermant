/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.sermant.otel;

import static io.sermant.otel.ApacheHttpClientRequest.headersToList;

import io.opentelemetry.instrumentation.api.semconv.http.HttpClientAttributesGetter;

import org.apache.http.HttpResponse;

import java.util.List;

final class ApacheHttpClientHttpAttributesGetter
        implements HttpClientAttributesGetter<ApacheHttpClientRequest, HttpResponse> {
    @Override
    public String getHttpRequestMethod(ApacheHttpClientRequest request) {
        return request.getMethod();
    }

    @Override
    public String getUrlFull(ApacheHttpClientRequest request) {
        return request.getUrl();
    }

    @Override
    public List<String> getHttpRequestHeader(ApacheHttpClientRequest request, String name) {
        return request.getHeader(name);
    }

    @Override
    public Integer getHttpResponseStatusCode(
            ApacheHttpClientRequest request, HttpResponse response, Throwable error) {
        return response.getStatusLine().getStatusCode();
    }

    @Override
    public List<String> getHttpResponseHeader(
            ApacheHttpClientRequest request, HttpResponse response, String name) {
        return headersToList(response.getHeaders(name));
    }

    @Override
    public String getNetworkProtocolName(
            ApacheHttpClientRequest request, HttpResponse response) {
        return request.getProtocolName();
    }

    @Override
    public String getNetworkProtocolVersion(
            ApacheHttpClientRequest request, HttpResponse response) {
        return request.getProtocolVersion();
    }

    @Override
    public String getServerAddress(ApacheHttpClientRequest request) {
        return request.getServerAddress();
    }

    @Override
    public Integer getServerPort(ApacheHttpClientRequest request) {
        return request.getServerPort();
    }
}
