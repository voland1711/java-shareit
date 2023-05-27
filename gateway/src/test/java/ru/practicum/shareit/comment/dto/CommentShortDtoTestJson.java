package ru.practicum.shareit.comment.dto;

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
import ru.practicum.shareit.comment.Dto.CommentShortDto;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
public class CommentShortDtoTestJson {
    @Autowired
    private JacksonTester<CommentShortDto> commentShortDtoJacksonTester;
    private Validator validator;
    private ObjectMapper objectMapper;

    private JSONObject jsonCommentShortDto = new JSONObject()
            .put("text", "text");

    public CommentShortDtoTestJson() throws JSONException {
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
    void serializeCommentDtoTest() {
        CommentShortDto commentShortDto = new CommentShortDto("text");

        JsonContent<CommentShortDto> commentShortDtoJsonContent = commentShortDtoJacksonTester.write(commentShortDto);
        assertThat(commentShortDtoJsonContent).hasJsonPath("$.text");
        assertThat(commentShortDtoJsonContent).extractingJsonPathStringValue("$.text").isEqualTo(commentShortDto.getText());

        CommentShortDto commentShortDtoForTest = commentShortDtoJacksonTester.parseObject(commentShortDtoJsonContent.getJson());
        assertThat(commentShortDtoForTest).isEqualTo(commentShortDto);
    }

    @SneakyThrows
    @Test
    @DisplayName("Поле 'text' является пустым")
    void commentShortDtoNameIsBlankTest() {
        String jsonItemDto = jsonCommentShortDto.put("text", "").toString();

        CommentShortDto commentShortDto = objectMapper.readValue(jsonItemDto, CommentShortDto.class);
        Set<ConstraintViolation<CommentShortDto>> violation = validator.validate(commentShortDto);

        assertEquals(1, violation.size());

        String errorMessage = "не должно быть пустым";
        violation.stream()
                .map(ConstraintViolation::getMessage)
                .forEach(message -> assertEquals(errorMessage, message));
    }

}
