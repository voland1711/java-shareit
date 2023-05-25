package ru.practicum.shareit.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Getter
@Setter
@NotNull
@AllArgsConstructor
@EqualsAndHashCode
public class UserDto implements Serializable {

    @JsonProperty("id")
    private Long id;

    @NotBlank
    @JsonProperty("name")
    private String name;

    @Email
    @NotNull
    @JsonProperty("email")
    private String email;
}

