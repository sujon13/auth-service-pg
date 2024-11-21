package example.demo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Service
@Slf4j
@RequiredArgsConstructor
public class RestApiService {
    private final RestClient restClient;

    public <T> void postWithoutBody(String url, T body) {
        try {
            restClient.post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), (request, response) -> {
                        log.error("Error Response Status: {}", response.getStatusCode());
                        //throw new ArticleNotFoundException(response);
                    })
                    .toBodilessEntity();
        } catch (RestClientException e) {
            log.error(e.getMessage());
        }
    }
}
