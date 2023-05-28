package ru.practicum.shareit.request.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.Map;

@Service
public class RequestClient extends BaseClient {

    private static final String API_PREFIX = "/requests";

    @Autowired
    public RequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public RequestClient(RestTemplate template) {
        super(template);
    }


    public ResponseEntity<Object> createItemRequest(Long userId, ItemRequestDto itemRequestDto) {
        return post("", userId, itemRequestDto);
    }

    public ResponseEntity<Object> getByIdItemRequest(Long userId, Long requestId) {
        return get("/" + requestId, userId);
    }

    public ResponseEntity<Object> getAllItemRequest(Long userId, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "from", from == null ? "" : from,
                "size", size == null ? "" : size
        );
        return get("/all?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> getAllByRequester(Long userId, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "from", from == null ? "" : from,
                "size", size == null ? "" : size
        );
        return get("?from={from}&size={size}", userId, parameters);
    }
}
