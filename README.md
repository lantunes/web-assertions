# WebAssertions
---------------

WebAssertions are assertions for the web. They make testing HTTP server-side
code simple.

## Examples
-----------

### Getting Started

First you'll need to clone this project and build it, and use the resulting jar. 
WebAssertions will eventually move to Maven central.

Import the WebAssertions library statically:

```java
import static org.bigtesting.WebAssertions.*;
```

Be sure to create a new web client before each test:

```java
@Before
public void beforeEachTest() throws Exception {
    newWebClient();
}
```

...then you can make a simple GET request:

```java
assertRequest("http://localhost:8080/")
    .producesPage()
    .withH1Tag(withContent("Hello"));
```

### Concurrent Requests

Making concurrent requests is easy:

```java
assertClients(
    new Client("client-1") {
        public void onRequest() {
            assertRequest("http://localhost:8080/name/Joe")
                .producesPage()
                .withH1Tag(withContent("Hello Joe"));
        }
    }, 
    new Client("client-2") {
        public void onRequest() {
            assertRequest("http://localhost:8080/name/Tim")
                .producesPage()
                .withH1Tag(withContent("Hello Tim"));
        }
    })
    .canMakeConcurrentRequests();
```

You can also have, say, 10 copies of a client make the same 
request, say, 15 times:

```java
assertClients(
    new Client(10) {
        public void onRequest() {
            assertRequest("http://localhost:8080/name/Tim")
                .producesPage()
                .withH1Tag(withContent("Hello Tim"));
        }
    })
    .canMakeConcurrentRequests(15);
```

### Multiple Requests

Requests are stateful, using the same web client:

```java
assertRequest(PUT, "http://localhost:8080/name/Joe")
    .producesPage()
    .withH1Tag(withContent("OK"));

assertRequest(GET, "http://localhost:8080/name")
    .producesPage()
    .withH1Tag(withContent("Name: Joe"));
```

...but only with the context of a thread:

```java
assertClients(
    new Client("client-1") {
        public void onRequest() {
            assertRequest(PUT, "http://localhost:8080/name/Joe")
                .producesPage()
                .withH1Tag(withContent("OK"));
            
            assertRequest(GET, "http://localhost:8080/name")
                .producesPage()
                .withH1Tag(withContent("Name: Joe"));
        }
    },
    new Client("client-2") {
        public void onRequest() {
            assertRequest(PUT, "http://localhost:8080/name/Tim")
                .producesPage()
                .withH1Tag(withContent("OK"));
            
            assertRequest(GET, "http://localhost:8080/name")
                .producesPage()
                .withH1Tag(withContent("Name: Tim"));
        }
    })
    .canMakeConcurrentRequests(10);
```

### Tear Down

Close the web client after each test:

```java
@After
public void afterEachTest() throws Exception {
    closeWebClient();
}
```