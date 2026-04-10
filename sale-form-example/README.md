# Sale Form Example

A minimal Java example demonstrating the [Sale Form](https://doc.payneteasy.eu/integration/api_use_cases/sale_form.html) payment flow using the Payneteasy SDK.

The application sends a Sale Form request to the Payment Gateway and prints the redirect URL. The customer should be redirected to this URL in a browser to complete the payment.

## Prerequisites

- Java 21+
- Maven 3.8+

## Build

```bash
./mvnw package
```

## Run

```bash
java -jar target/sale-form-example-1.0-SNAPSHOT.jar \
  --api-base-url https://sandbox.payneteasy.eu/paynet \
  --endpoint-id  123 \
  --login        my-login \
  --control-key  my-control-key
```

### Optional parameters

| Parameter    | Default | Description        |
|--------------|---------|--------------------|
| `--amount`   | `1.00`  | Payment amount     |
| `--currency` | `EUR`   | Payment currency   |

## Maven configuration

Add the Payneteasy repository and dependencies to your `pom.xml`:

```xml
<repositories>
    <repository>
        <id>payneteasy-sdk-repo</id>
        <url>https://paynet-qa.clubber.me/reader/maven</url>
    </repository>

    <repository>
        <id>public-payneteasy-repo</id>
        <url>https://maven.pne.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.payneteasy.paynet</groupId>
        <artifactId>paynet-remote-api-client</artifactId>
        <version>${paynet.version}</version>
    </dependency>

    <dependency>
        <groupId>com.payneteasy.paynet</groupId>
        <artifactId>paynet-remote-api</artifactId>
        <version>${paynet.version}</version>
    </dependency>

    <dependency>
        <groupId>com.payneteasy.http-client</groupId>
        <artifactId>http-client-impl</artifactId>
        <version>1.0-8</version>
    </dependency>
</dependencies>
```

## Code example

```java
// Build the HTTP client
ControlClientHttpService httpClient = new ControlClientHttpService(
        new HttpClientImpl()
        , HttpRequestParameters.builder()
                .timeouts(new HttpTimeouts(10_000, 20_000))
                .build()
        , new HttpHeaders(List.of())
);

// Build the Payneteasy service
IPaynetService paynet = new DynamicClientBuilder()
        .login      ( "my-login"                                )
        .baseUrl    ( "https://sandbox.payneteasy.eu/paynet"    )
        .controlKey ( "my-control-key"                          )
        .httpClient ( httpClient                                )
        .build();

// Prepare the sale form request
SaleFormRequest request = new SaleFormRequest();
request.setClientOrderId   ( "order-123"              );
request.setAmount          ( new BigDecimal("10.00")  );
request.setCurrency        ( "EUR"                    );
request.setOrderDescription( "Test payment"           );
request.setEmail           ( "customer@example.com"   );
request.setIpAddress       ( "1.2.3.4"                );
request.setCountry         ( "FR"                     );
request.setAddress1        ( "10 Rue de la Paix"      );
request.setZipCode         ( "75002"                  );
request.setCity            ( "Paris"                  );

// Send the request and get the redirect URL
AsyncCheckoutFormRedirect result = paynet.startSaleForm(endpointId, request);

System.out.println("Redirect URL: " + result.getRedirectUrl());
```

The customer should be redirected to the returned URL in a browser to complete the payment.

## Dependencies

Only the minimum required libraries are included:

| Dependency                 | Purpose                            |
|----------------------------|------------------------------------|
| `paynet-remote-api-client` | SDK client for the Payment Gateway |
| `paynet-remote-api`        | SDK API interfaces and models      |
| `http-client-impl`         | HTTP transport                     |
| `slf4j-stable`             | Logging                            |
| `picocli`                  | Command-line argument parsing      |
