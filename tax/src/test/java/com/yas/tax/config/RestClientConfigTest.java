package com.yas.tax.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

class RestClientConfigTest {

    @Test
    void restClient_shouldBuildClient() {
        RestClientConfig config = new RestClientConfig();

        RestClient restClient = config.restClient();

        assertThat(restClient).isNotNull();
    }
}
