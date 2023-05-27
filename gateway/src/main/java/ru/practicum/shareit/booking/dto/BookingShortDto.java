package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode
public class BookingShortDto implements Serializable {

    @NotNull
    private Long itemId;

    @NotNull
    @Future
    @JsonProperty("start")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime start;

    @NotNull
    @Future
    @JsonProperty("end")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime end;

}
