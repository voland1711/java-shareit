package ru.practicum.shareit.user.Dto;

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
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
public class UserDtoTestJson {
    @Autowired
    private JacksonTester<UserDto> userDtoJacksonTester;
    private Validator validator;
    private ObjectMapper objectMapper;

    private JSONObject jsonUser = new JSONObject()
            .put("name", "name")
            .put("email", "name@name.ru");

    public UserDtoTestJson() throws JSONException {
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
    void serializeUserDtoTest() {
        UserDto userDto = new UserDto().toBuilder()
                .id(1L)
                .name("User")
                .email("user@user.com")
                .build();

        JsonContent<UserDto> userDtoJsonContent = userDtoJacksonTester.write(userDto);
        assertThat(userDtoJsonContent).hasJsonPath("$.id");
        assertThat(userDtoJsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(userDto.getId().intValue());
        assertThat(userDtoJsonContent).hasJsonPath("$.name");
        assertThat(userDtoJsonContent).extractingJsonPathStringValue("$.name").isEqualTo(userDto.getName());
        assertThat(userDtoJsonContent).hasJsonPath("$.email");
        assertThat(userDtoJsonContent).extractingJsonPathStringValue("$.email").isEqualTo(userDto.getEmail());

        UserDto userDtoForTest = userDtoJacksonTester.parseObject(userDtoJsonContent.getJson());
        assertThat(userDtoForTest).isEqualTo(userDto);
    }

    @SneakyThrows
    @Test
    @DisplayName("Поле 'name' является пустым")
    void userDtoNameIsBlankTest() {
        String jsonUserDto = jsonUser.put("name", "").toString();

        UserDto userDto = objectMapper.readValue(jsonUserDto, UserDto.class);
        Set<ConstraintViolation<UserDto>> violation = validator.validate(userDto);

        assertEquals(1, violation.size());

        String errorMessage = "не должно быть пустым";
        violation.stream()
                .map(ConstraintViolation::getMessage)
                .forEach(message -> assertEquals(errorMessage, message));
    }

    @SneakyThrows
    @Test
    @DisplayName("Поле 'email' не является валидным")
    void userDtoEmailIsNotValidTest() {
        String jsonUserDto = jsonUser.put("email", "name0name.ru").toString();

        UserDto userDto = objectMapper.readValue(jsonUserDto, UserDto.class);
        Set<ConstraintViolation<UserDto>> violation = validator.validate(userDto);

        assertEquals(1, violation.size());

        String errorMessage = "должно иметь формат адреса электронной почты";
        violation.stream()
                .map(ConstraintViolation::getMessage)
                .forEach(message -> assertEquals(errorMessage, message));
    }


}
