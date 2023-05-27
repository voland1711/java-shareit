package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.ArrayList;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
public class ItemDtoTestJson {
    @Autowired
    private JacksonTester<ItemDto> userDtoJacksonTester;
    private Validator validator;
    private ObjectMapper objectMapper;

    private JSONObject jsonItem = new JSONObject()
            .put("name", "name")
            .put("description", "description1")
            .put("available", true);

    public ItemDtoTestJson() throws JSONException {
    }

    @BeforeEach
    void setUp() {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();

        JacksonTester.initFields(this, new ObjectMapper());

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    @SneakyThrows
    @Test
    void serializeItemDtoTest() {
        ItemDto itemDto = new ItemDto(1L, "item", "descriptionItem", true, 2L, new ArrayList<>());

        JsonContent<ItemDto> itemDtoJsonContentDtoJsonContent = userDtoJacksonTester.write(itemDto);
        assertThat(itemDtoJsonContentDtoJsonContent).hasJsonPath("$.id");
        assertThat(itemDtoJsonContentDtoJsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(itemDto.getId().intValue());

        assertThat(itemDtoJsonContentDtoJsonContent).hasJsonPath("$.name");
        assertThat(itemDtoJsonContentDtoJsonContent).extractingJsonPathStringValue("$.name").isEqualTo(itemDto.getName());
        assertThat(itemDtoJsonContentDtoJsonContent).hasJsonPath("$.description");
        assertThat(itemDtoJsonContentDtoJsonContent).extractingJsonPathStringValue("$.description").isEqualTo(itemDto.getDescription());
        assertThat(itemDtoJsonContentDtoJsonContent).hasJsonPath("$.available");
        assertThat(itemDtoJsonContentDtoJsonContent).extractingJsonPathBooleanValue("$.available").isEqualTo(itemDto.getAvailable());


        ItemDto itemDtoForTest = userDtoJacksonTester.parseObject(itemDtoJsonContentDtoJsonContent.getJson());
        assertThat(itemDtoForTest).isEqualTo(itemDto);
    }

    @SneakyThrows
    @Test
    @DisplayName("Поле 'name' является пустым")
    void itemDtoNameIsBlankTest() {
        String jsonItemDto = jsonItem.put("name", "").toString();

        ItemDto itemDto = objectMapper.readValue(jsonItemDto, ItemDto.class);
        Set<ConstraintViolation<ItemDto>> violation = validator.validate(itemDto);

        assertEquals(1, violation.size());

        String errorMessage = "не должно быть пустым";
        violation.stream()
                .map(ConstraintViolation::getMessage)
                .forEach(message -> assertEquals(errorMessage, message));
    }

    @SneakyThrows
    @Test
    @DisplayName("Поле 'description' является пустым")
    void itemDtoDescriptionIsBlankTest() {
        String jsonItemDto = jsonItem.put("description", "").toString();

        ItemDto itemDto = objectMapper.readValue(jsonItemDto, ItemDto.class);
        Set<ConstraintViolation<ItemDto>> violation = validator.validate(itemDto);

        assertEquals(1, violation.size());

        String errorMessage = "не должно быть пустым";
        violation.stream()
                .map(ConstraintViolation::getMessage)
                .forEach(message -> assertEquals(errorMessage, message));
    }

    @SneakyThrows
    @Test
    @DisplayName("Поле 'available' является пустым")
    void itemDtoAvailableIsBlankTest() {
        String jsonItemDto = jsonItem.put("available", "").toString();

        ItemDto itemDto = objectMapper.readValue(jsonItemDto, ItemDto.class);
        Set<ConstraintViolation<ItemDto>> violation = validator.validate(itemDto);

        assertEquals(1, violation.size());

        String errorMessage = "не должно равняться null";
        violation.stream()
                .map(ConstraintViolation::getMessage)
                .forEach(message -> assertEquals(errorMessage, message));
    }

    @SneakyThrows
    @Test
    @DisplayName("Поле 'requestId' является пустым")
    void itemDtoRequestIdIsBlankTest() {
        String jsonItemDto = jsonItem.put("requestId", "").toString();

        ItemDto itemDto = objectMapper.readValue(jsonItemDto, ItemDto.class);
        Set<ConstraintViolation<ItemDto>> violation = validator.validate(itemDto);

        assertEquals(0, violation.size());

    }

}
