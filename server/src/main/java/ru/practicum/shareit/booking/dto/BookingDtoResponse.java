package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode
public class BookingDtoResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("bookerId")
    private Long bookerId;
}
