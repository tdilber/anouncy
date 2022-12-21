package com.beyt.anouncy.search.helper;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.elasticsearch.client.RestClient;

import javax.net.ssl.SSLContext;
import java.net.URI;

@Slf4j
public class ElasticSearchHelper {


    public static RestClient createRestClient(String url, String username, String password) throws Exception {
        try {
            HttpHost httpHost = URIUtils.extractHost(URI.create(url));
            SSLContextBuilder sslBuilder = SSLContexts.custom()
                    .loadTrustMaterial(null, (x509Certificates, s) -> true);
            final CredentialsProvider credentialsProvider =
                    new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY,
                    new UsernamePasswordCredentials(username, password));

            final SSLContext sslContext = sslBuilder.build();
            RestClient client = RestClient
                    .builder(httpHost)
                    .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder
                            .setDefaultCredentialsProvider(credentialsProvider)
                            .setSSLContext(sslContext)
                            .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE))
                    .setRequestConfigCallback(requestConfigBuilder -> requestConfigBuilder.setConnectTimeout(5000)
                            .setSocketTimeout(120000))
                    .build();
            log.info("Elasticsearch Client Created");
            return client;
        } catch (Exception e) {
            log.error("Elasticsearch Client Error msg: " + e.getMessage(), e);
            throw new Exception("Could not create an elasticsearch client!!");
        }
    }
}
