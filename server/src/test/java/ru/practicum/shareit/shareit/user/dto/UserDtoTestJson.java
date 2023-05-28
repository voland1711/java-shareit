package ru.practicum.shareit.shareit.user.dto;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import static org.assertj.core.api.Assertions.assertThat;

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

}
