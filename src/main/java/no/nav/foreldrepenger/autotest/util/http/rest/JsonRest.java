package no.nav.foreldrepenger.autotest.util.http.rest;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.qameta.allure.Attachment;
import no.nav.foreldrepenger.autotest.util.http.HttpSession;

public abstract class JsonRest extends Rest {

    private static String ACCEPT_JSON_HEADER = "application/json";

    public JsonRest(HttpSession session) {
        super(session);
    }

    /*
     * POST
     */
    protected HttpResponse postJson(String url, Object object) {
        return postJson(url, toJson(object));
    }

    protected HttpResponse postJson(String url, String json) {
        Map<String, String> headers = new HashMap<>();
        return postJson(url, json, headers);
    }

    protected HttpResponse postJson(String url, String json, Map<String, String> headers) {
        headers.put("Accept", ACCEPT_JSON_HEADER);
        return post(url, hentJsonPostEntity(json), headers);
    }

    protected <T> T postOgHentJson(String url, Object requestData, JavaType returnType, StatusRange expectedStatusRange) {
        return postOgHentJson(url, requestData, new HashMap<>(), returnType, expectedStatusRange);
    }

    protected <T> T postOgHentJson(String url, Object requestData, Class<T> returnType, StatusRange expectedStatusRange) {
        return postOgHentJson(url, requestData, new HashMap<>(), returnType, expectedStatusRange);
    }

    protected <T> T postOgHentJson(String url, Object requestData, Map<String, String> headers, Class<T> returnType, StatusRange expectedStatusRange)
    {
        String json = postOgVerifiser(url, requestData, headers, expectedStatusRange);
        return json.equals("") ? null : fromJson(json, returnType);
    }

    protected <T> T postOgHentJson(String url, Object requestData, Map<String, String> headers, JavaType returnType, StatusRange expectedStatusRange)
    {
        String json = postOgVerifiser(url, requestData, headers, expectedStatusRange);
        return json.equals("") ? null : fromJson(returnType, json);
    }

    protected String postOgVerifiser(String url, Object requestData, StatusRange expectedStatusRange) {
        return postOgVerifiser(url, requestData, new HashMap<>(), expectedStatusRange);
    }

    protected String postOgVerifiser(String url, Object requestData, Map<String, String> headers, StatusRange expectedStatusRange) {
        String request = requestData == null ? "{}" : toJson(requestData);
        HttpResponse response = postJson(url, request, headers);
        String json = hentResponseBody(response);
        if (expectedStatusRange != null) {
            ValidateResponse(response, expectedStatusRange, url + "\n" + request + "\n\n" + json);
        }
        return json;
    }

    /*
     * GET
     */
    protected HttpResponse getJson(String url) {
        return getJson(url, new HashMap<>());
    }

    protected HttpResponse getJson(String url, Map<String, String> headers, Map<String, String> data) {
        return getJson(url + UrlEncodeQuery(data), headers);
    }
    protected HttpResponse getJson(String url, Map<String, String> headers) {
        headers.put("Accept", ACCEPT_JSON_HEADER);
        return get(url, headers);
    }

    protected <T> T getOgHentJson(String url, Class<T> returnType, StatusRange expectedStatusRange) {
        return getOgHentJson(url, new HashMap<>(), returnType, expectedStatusRange);
    }
    protected <T> T getOgHentJson(String url, JavaType returnType, StatusRange expectedStatusRange) {
        return getOgHentJson(url, new HashMap<>(), returnType, expectedStatusRange);
    }

    protected <T> T getOgHentJson(String url, Map<String, String> headers, Class<T> returnType, StatusRange expectedStatusRange) {
        HttpResponse response = getJson(url, headers);
        String json = hentResponseBody(response);
        ValidateResponse(response, expectedStatusRange, url + "\n\n" + json);
        return json.equals("") ? null : fromJson(json, returnType);
    }

    protected <T> T getOgHentJson(String url, Map<String, String> headers, JavaType returnType, StatusRange expectedStatusRange) {
        HttpResponse response = getJson(url, headers);
        String json = hentResponseBody(response);
        ValidateResponse(response, expectedStatusRange, url + "\n\n" + json);
        return json.equals("") ? null : fromJson(returnType, json);
    }

    protected <T> T postFormOgHentJson(String url, Map<String, String> query, Class<T> returnType) {
        String content = UrlEncodeQuery(query,"");
        HttpEntity entity = new StringEntity(content, ContentType.APPLICATION_FORM_URLENCODED);
        Map<String,String> headers = Map.of("Content-Type", ContentType.APPLICATION_FORM_URLENCODED.getMimeType());
        HttpResponse response = post(url, entity, headers);
        String json = hentResponseBody(response);
        return json.equals("") ? null : fromJson(json, returnType);
    }


    /*
     * PUT
     */

    protected HttpResponse putJson(String url, Object requestData, StatusRange expectedStatusRange) {
        String json = toJson(requestData);
        return putJson(url, json, expectedStatusRange);
    }
    protected HttpResponse putJson(String url, String json, StatusRange expectedStatusRange) {
        HttpResponse response = put(url, hentJsonPostEntity(json));
        ValidateResponse(response, expectedStatusRange);
        return response;
    }

    protected StringEntity hentJsonPostEntity(String json) {
        try {
            return new StringEntity(logJsonSomAttachment(json), ContentType.APPLICATION_JSON);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
    }
    protected ObjectMapper hentObjectMapper() {
        return JsonKlient.getObjectMapper();
    }

    @Attachment(value = "HttpRequest", type = "application/json")
    private String logJsonSomAttachment(String json) {
        return json;
    }

    private String toJson(Object object) {
        try {
            return hentObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T fromJson(String json, Class<T> returnType) {
        try {
            return hentObjectMapper().readValue(json, returnType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> T fromJson(JavaType returnType, String json) {
        try {
            return hentObjectMapper().readValue(json, returnType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
