package indi.mofan.product;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProductControllerTest implements WithAssertions {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testGetProduct() {
        ResponseEntity<String> entity = restTemplate.getForEntity("http://localhost:" + port + "/product/1", String.class);
        assertThat(entity.getStatusCode().is2xxSuccessful()).isTrue();
    }
}

