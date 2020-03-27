/**
 * Copyright (c) 2015, Salesforce.com, Inc. All rights reserved.
 * Copyright (c) 2020, Bitshift (bitshifted.co), Inc. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer.
 * 
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions
 * and the following disclaimer in the documentation and/or other materials provided with the
 * distribution.
 * 
 * Neither the name of Salesforce.com nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 * WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package co.bitshfted.xapps.zsync.internal.util;

import co.bitshfted.xapps.zsync.http.ContentRange;
import co.bitshfted.xapps.zsync.http.Credentials;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.ParseException;
import java.util.*;

import static co.bitshfted.xapps.zsync.internal.util.EventLogHttpTransferListener.*;
import static co.bitshfted.xapps.zsync.internal.util.ZsyncClient.*;
import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

public class ZsyncClientTest {

  @Test(expected = IOException.class)
  public void testGetBoundaryNoAttributeValue() throws IOException {
    ZsyncClient.getBoundary(MediaType.create("multipart", "byteranges"));
  }

  @Test(expected = IOException.class)
  public void testGetBoundaryInvalidSubtype() throws IOException {
    ZsyncClient.getBoundary(MediaType.create("multipart", "mixed"));
  }

  @Test
  public void testGetBoundary() throws IOException {
    final String expected = "gc0p4Jq0M2Yt08jU534c0p";
    final Map<String, List<String>> parameters = new HashMap<>();
    parameters.put("boundary", List.of(expected));
    Assert.assertArrayEquals(expected.getBytes(ISO_8859_1), ZsyncClient.getBoundary(MediaType.create("multipart", "byteranges", parameters)));
  }

  @Test
  public void testParseContentTypeNull() throws IOException, URISyntaxException {
    var headers = new HashMap<String, List<String>>();
    final HttpResponse response = fakeResponse(200, headers);
    assertNull(ZsyncClient.parseContentType(response));
  }


  @Test
  public void testParseContentTypeMultipart() throws IOException, URISyntaxException {
    var headers = new HashMap<String, List<String>>();
    headers.put("Content-Type", List.of("multipart/byteranges;boundary=gc0p4Jq0M2Yt08jU534c0p"));
    final HttpResponse response = fakeResponse(200, headers);

    final Map<String, List<String>> parameters = new HashMap<>();
    parameters.put("boundary", List.of("gc0p4Jq0M2Yt08jU534c0p"));
    Assert.assertEquals(
        MediaType.create("multipart", "byteranges", parameters), ZsyncClient.parseContentType(response));
  }

  @Test(expected = ParseException.class)
  public void testParseContentRangeInvalidBytesUnit() throws ParseException {
    ZsyncClient.parseContentRange("byte 1-3/3");
  }

  @Test(expected = ParseException.class)
  public void testParseContentRangeMissingSeparator() throws ParseException {
    ZsyncClient.parseContentRange("bytes 1 3/3");
  }

  @Test(expected = ParseException.class)
  public void testParseContentRangeMissingFirst() throws ParseException {
    ZsyncClient.parseContentRange("bytes -3/3");
  }

  @Test(expected = ParseException.class)
  public void testParseContentRangeInvalidFirst() throws ParseException {
    ZsyncClient.parseContentRange("bytes a-3/3");
  }

  @Test(expected = ParseException.class)
  public void testParseContentRangeNegativeFirst() throws ParseException {
    ZsyncClient.parseContentRange("bytes -1-3/5");
  }

  @Test(expected = ParseException.class)
  public void testParseContentRangeMissingLast() throws ParseException {
    ZsyncClient.parseContentRange("bytes 1-/3");
  }

  @Test(expected = ParseException.class)
  public void testParseContentRangeInvalidLast() throws ParseException {
    ZsyncClient.parseContentRange("bytes 1-a/3");
  }

  @Test(expected = ParseException.class)
  public void testParseContentRangeNegativeLast() throws ParseException {
    ZsyncClient.parseContentRange("bytes 1--3/3");
  }

  @Test(expected = ParseException.class)
  public void testParseContentRangeMissingDash() throws ParseException {
    ZsyncClient.parseContentRange("bytes 1-3 3");
  }

  @Test
  public void testParseContentRangeMissingLength() throws ParseException {
    Assert.assertEquals(new ContentRange(1, 3), ZsyncClient.parseContentRange("bytes 1-3/"));
  }

  @Test
  public void testParseContentRangeInvalidLength() throws ParseException {
    Assert.assertEquals(new ContentRange(1, 3), ZsyncClient.parseContentRange("bytes 1-3/b"));
  }

  @Test
  public void testParseContentRangeIncorrectLength() throws ParseException {
    Assert.assertEquals(new ContentRange(1, 3), ZsyncClient.parseContentRange("bytes 1-3/4"));
  }

  @Test(expected = ParseException.class)
  public void testParseContentRangeFirstLargerLast() throws ParseException {
    ZsyncClient.parseContentRange("bytes 3-1/3");
  }

  @Test
  public void testParseContentRangeNoLength() throws ParseException {
    Assert.assertEquals(new ContentRange(1, 3), ZsyncClient.parseContentRange("bytes 1-3/*"));
  }

  @Test
  public void testParseContentRange() throws ParseException {
    Assert.assertEquals(new ContentRange(1, 3), ZsyncClient.parseContentRange("bytes 1-3/3"));
  }

  @Test
  public void exceptionThrownFromConstructorForNullHttpClient() {
    // Act
    try {
      new ZsyncClient(null);
    } catch (IllegalArgumentException exception) {

      // Assert
      assertEquals("httpClient cannot be null", exception.getMessage());
    }
  }

  @SuppressWarnings("unchecked")
  @Test
  public void runtimeExceptionThrownForIoExceptionDuringHttpCommunication() throws Exception {
    // Arrange
    HttpClient mockHttpClient = mock(HttpClient.class);
    HttpResponse mockResponse = mock(HttpResponse.class);
    when(mockHttpClient.send(any(HttpRequest.class), any())).thenReturn(mockResponse);
    when(mockResponse.statusCode()).thenReturn(200);

    byte[] responseBody = new byte[0];
    when(mockResponse.body()).thenReturn(responseBody);
    RangeReceiver mockReceiver = mock(RangeReceiver.class);
    RangeTransferListener listener = mock(RangeTransferListener.class);
    when(listener.newTransfer(any(List.class))).thenReturn(mock(HttpTransferListener.class));
    List<ContentRange> ranges = this.createSomeRanges(1);
    URI url = new URI("http://host/someurl");

    // Act
    try {
      new ZsyncClient(mockHttpClient).partialGet(url, ranges, Collections.<String, Credentials>emptyMap(), "zsync/1.0",
              mockReceiver, listener);
    } catch (IOException exception) {

      // Assert
      assertEquals("IO", exception.getMessage());
    }
  }

  @SuppressWarnings("unchecked")
  @Test
  public void runtimeExceptionThrownForHttpResponsesOtherThan206() throws IOException, URISyntaxException, InterruptedException {
    // Arrange
    List<Integer> responsesToTest = List.of(500, 413); // Add whatever other ones we want
    HttpClient mockHttpClient = mock(HttpClient.class);
    HttpResponse mockResponse = mock(HttpResponse.class);
    when(mockHttpClient.send(any(HttpRequest.class), any())).thenReturn(mockResponse);

    RangeReceiver mockReceiver = mock(RangeReceiver.class);
    RangeTransferListener listener = mock(RangeTransferListener.class);
    when(listener.newTransfer(any(List.class))).thenReturn(mock(HttpTransferListener.class));
    List<ContentRange> ranges = this.createSomeRanges(1);
    URI url = new URI("http://host/someurl");

    for (Integer responseToTest : responsesToTest) {

      // Arrange some more
      when(mockResponse.statusCode()).thenReturn(responseToTest);

      // Act
      try {
        new ZsyncClient(mockHttpClient).partialGet(url, ranges, Collections.emptyMap(), "zsync/1.0",
            mockReceiver, listener);
      } catch (ZsyncClient.HttpError exception) {
        assertEquals(responseToTest.intValue(), exception.getCode());
      }
    }

  }

  private HttpResponse fakeResponse(int code) throws URISyntaxException {
    HttpRequest fakeRequest = HttpRequest.newBuilder().uri(new URI("http://host/url")).build();
    return new DummyResponse(code, fakeRequest);
  }

  private HttpResponse fakeResponse(int code, Map<String, List<String>> headerMap) throws URISyntaxException {
    HttpRequest fakeRequest = HttpRequest.newBuilder().uri(new URI("http://host/url")).build();
    DummyResponse response = new DummyResponse(code, fakeRequest);
    response.setHeader(headerMap);
    return response;
  }

  @Test
  public void testTransferListener() throws Exception {
    final URI uri = URI.create("http://host/bla");

    final byte[] data = new byte[17];
    final HttpResponse response = mock(HttpResponse.class);
    when(response.body()).thenReturn(data);
    final InputStream inputStream = new ByteArrayInputStream(data);

    HttpRequest request = HttpRequest.newBuilder(uri).build();
    final HttpClient mockHttpClient = mock(HttpClient.class);
    when(mockHttpClient.send(any(HttpRequest.class), any())).thenReturn(response);
    when(response.statusCode()).thenReturn(200);
    when(response.request()).thenReturn(request);

    final EventLogHttpTransferListener listener = new EventLogHttpTransferListener();
    final InputStream in =
        new ZsyncClient(mockHttpClient).get(uri, Collections.emptyMap(), "zsync/1.0", listener);
    final byte[] b = new byte[8];
    assertEquals(0, in.read());
    assertEquals(8, in.read(b));
    assertEquals(8, in.read(b, 0, 8));
    assertEquals(-1, in.read());
    in.close();

    final List<Event> events =
        List.of(new Initialized(request), new Started(uri,
            data.length), new Transferred(1), new Transferred(8), new Transferred(8), Closed.INSTANCE);
    assertEquals(events, listener.getEventLog());
  }

  @Test
  public void testChallenges() throws Exception {
    final URI uri = URI.create("https://host/file");
    final Map<String, Credentials> credentials = Map.of("host", new Credentials("jdoe", "secret"));
    final String useragent = "zsync/1.0";
    final HttpClient httpClient = mock(HttpClient.class);
    final ZsyncClient zsyncClient = new ZsyncClient(httpClient);
    final HttpTransferListener listener = mock(HttpTransferListener.class);

    // expect request without authorization header at first and then with
    HttpResponse mockResponse = mock(HttpResponse.class);
    when(httpClient.send(any(HttpRequest.class), any())).thenReturn(mockResponse);
    when(mockResponse.statusCode()).thenReturn(401, 200, 200);
    HttpHeaders headers = HttpHeaders.of(Map.of("WWW-Authenticate", List.of("something")), ((s1,s2) -> true));
    when(mockResponse.headers()).thenReturn(headers);
    byte[] body = new byte[0];
    when(mockResponse.body()).thenReturn(body);
    zsyncClient.get(uri, credentials, useragent, listener);

    // subsequent https calls to same host should auth right away without challenge
    zsyncClient.get(uri, credentials, useragent, listener);

  }


  private List<ContentRange> createSomeRanges(int numberOfRangesToCreate) {
    List<ContentRange> ranges = new ArrayList<>(numberOfRangesToCreate);
    int rangeStart = 0;
    int rangeSize = 10;
    for (int i = 0; i < numberOfRangesToCreate; i++) {
      rangeStart = i * rangeSize;
      int rangeEnd = rangeStart + rangeSize - 1;
      ranges.add(new ContentRange(rangeStart, rangeEnd));
    }
    return ranges;
  }

}
