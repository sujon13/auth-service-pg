package example.demo.service;

import example.demo.model.UserResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@Slf4j
@RequiredArgsConstructor
public class ApiCallService {
    private final RestClient restClient;

    public UserResponseDto handleApiCall() {
        String uri = "https://jsonplaceholder.typicode.com/todos/1";
        UserResponseDto userResponseDto = restClient.get()
                .uri(uri)
                .retrieve()
                .body(UserResponseDto.class);
        System.out.println(userResponseDto);
        return userResponseDto;
    }

}
