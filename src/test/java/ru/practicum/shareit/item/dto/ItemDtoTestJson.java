package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemDtoTestJson {

    @Autowired
    private JacksonTester<UserDto> userDtoJacksonTester;

    @Test
    void serializeUserDtoTest () throws Exception {
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
