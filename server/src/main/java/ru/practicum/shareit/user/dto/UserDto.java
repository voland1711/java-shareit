package ru.practicum.shareit.user.dto;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
@EqualsAndHashCode
public class UserDto {

    private Long id;

    private String name;

    private String email;
}
