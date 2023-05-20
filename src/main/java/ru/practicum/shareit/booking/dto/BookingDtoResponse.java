package ru.practicum.shareit.booking.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode
public class BookingDtoResponse {
    private Long id;
    private Long bookerId;
}
