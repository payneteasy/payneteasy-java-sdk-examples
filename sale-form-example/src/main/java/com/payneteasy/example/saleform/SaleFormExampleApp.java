package com.payneteasy.example.saleform;

import com.payneteasy.http.client.api.HttpHeaders;
import com.payneteasy.http.client.api.HttpRequestParameters;
import com.payneteasy.http.client.api.HttpTimeouts;
import com.payneteasy.http.client.impl.HttpClientImpl;
import com.payneteasy.paynet.processing.IPaynetService;
import com.payneteasy.paynet.processing.client.ControlClientHttpService;
import com.payneteasy.paynet.processing.client.dynamic.DynamicClientBuilder;
import com.payneteasy.paynet.processing.request.SaleFormRequest;
import com.payneteasy.paynet.processing.response.AsyncCheckoutFormRedirect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "sale-form", description = "Sale Form Example")
public class SaleFormExampleApp implements Callable<Integer> {

    private static final Logger LOG = LoggerFactory.getLogger( SaleFormExampleApp.class );

    @CommandLine.Option(names = "--api-base-url", required = true, description = "Base API URL")
    String baseApiUrl;

    @CommandLine.Option(names = "--endpoint-id", required = true, description = "Uniquely identified terminal in Payment Gateway")
    int endpointId;

    @CommandLine.Option(names = "--login", required = true, description = "Merchant login")
    String login;

    @CommandLine.Option(names = "--control-key", required = true, description = "Merchant control key")
    String controlKey;

    @CommandLine.Option(names = "--amount", defaultValue = "1.00")
    BigDecimal amount;

    @CommandLine.Option(names = "--currency", defaultValue = "EUR")
    String currency;

    @Override
    public Integer call() throws Exception {
        String invoice = UUID.randomUUID().toString();

        SaleFormRequest saleFormRequest = new SaleFormRequest();
        saleFormRequest.setClientOrderId(invoice);

        saleFormRequest.setOrderDescription ( "Description for " + invoice );

        saleFormRequest.setAmount    ( amount             );
        saleFormRequest.setCurrency  ( currency           );

        saleFormRequest.setEmail     ( "test@mail.com"    );
        saleFormRequest.setIpAddress ( "1.2.3.4"          );

        saleFormRequest.setCountry  ( "FR"                );
        saleFormRequest.setAddress1 ( "10 Rue de la Paix" );
        saleFormRequest.setZipCode  ( "75002"             );
        saleFormRequest.setCity     ( "Paris"             );

        ControlClientHttpService httpClient = new ControlClientHttpService(
                new HttpClientImpl()
                , HttpRequestParameters
                        .builder()
                        .timeouts(
                                new HttpTimeouts(
                                        10_000
                                        , 20_000
                                )
                        )
                        .build()
                , new HttpHeaders(List.of())
        );

        IPaynetService paynet = new DynamicClientBuilder()
                .login      ( login      )
                .baseUrl    ( baseApiUrl )
                .controlKey ( controlKey )
                .httpClient ( httpClient )
                .build();

        LOG.info("Sending sale form request to {} ...", baseApiUrl);
        AsyncCheckoutFormRedirect redirectResult = paynet.startSaleForm(endpointId, saleFormRequest);

        LOG.info("Order id is {}", redirectResult.getPaynetOrderId());
        LOG.info("Redirect URL is {}}", redirectResult.getRedirectUrl());

        return 0;
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new SaleFormExampleApp()).execute(args);
        System.exit(exitCode);
    }
}
