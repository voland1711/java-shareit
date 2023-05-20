package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;


@JsonTest
@RunWith(SpringRunner.class)
class BookingShortDtoTest {
    @Autowired
    private JacksonTester<BookingShortDto> bookingShortDtoJacksonTester;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private LocalDateTime start = LocalDateTime.parse(LocalDateTime.now().plusMinutes(2).format(dateTimeFormatter));
    private LocalDateTime end = LocalDateTime.parse(LocalDateTime.now().plusMinutes(10).format(dateTimeFormatter));
    private Validator validator;
    private ObjectMapper objectMapper;

    private JSONObject jsonBookingShort = new JSONObject()
            .put("itemId", 1L)
            .put("start", start)
            .put("end", end);

    BookingShortDtoTest() throws JSONException {
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

    @Test
    @DisplayName("Поля объекта заполнены валидными данными")
    void serializeBookingShortDtoTest() throws Exception {
        BookingShortDto bookingShortDto = new BookingShortDto().toBuilder()
                .itemId(1L)
                .start(start)
                .end(end)
                .build();

        JsonContent<BookingShortDto> bookingShortDtoJsonContent = bookingShortDtoJacksonTester.write(bookingShortDto);
        assertThat(bookingShortDtoJsonContent).hasJsonPath("$.itemId");
        assertThat(bookingShortDtoJsonContent).extractingJsonPathNumberValue("$.itemId")
                .isEqualTo(bookingShortDto.getItemId().intValue());
        assertThat(bookingShortDtoJsonContent).hasJsonPath("$.start");
        assertThat(bookingShortDtoJsonContent).extractingJsonPathStringValue("$.start")
                .isEqualTo((bookingShortDto.getStart().format(dateTimeFormatter)));
        assertThat(bookingShortDtoJsonContent).hasJsonPath("$.end");
        assertThat(bookingShortDtoJsonContent).extractingJsonPathStringValue("$.end")
                .isEqualTo((bookingShortDto.getEnd().format(dateTimeFormatter)));
        BookingShortDto bookingShortDtoForTest = bookingShortDtoJacksonTester
                .parseObject(bookingShortDtoJsonContent.getJson());
        assertThat(bookingShortDtoForTest).isEqualTo(bookingShortDto);
    }

    @Test
    @SneakyThrows
    @DisplayName("Обработка JSON из строки")
    void parsingJsonFromStringInObjectTest() {
        BookingShortDto dto = BookingShortDto.builder()
                .itemId(1L)
                .start(start)
                .end(end)
                .build();
        String expectedJson = "{"
                + "\"itemId\":1,"
                + "\"start\":\"" + start + "\","
                + "\"end\":\"" + end + "\""
                + "}";
        JsonContent<BookingShortDto> jsonContent = bookingShortDtoJacksonTester.write(dto);
        assertThat(jsonContent).isEqualToJson(expectedJson);
        assertThat(jsonContent).hasJsonPathStringValue("$.start");
        assertThat(jsonContent).extractingJsonPathStringValue("$.start")
                .isEqualTo(bookingShortDtoJacksonTester.parseObject(expectedJson).getStart().toString());
        assertThat(jsonContent).hasJsonPathStringValue("$.end");
        assertThat(jsonContent).extractingJsonPathStringValue("$.end")
                .isEqualTo(bookingShortDtoJacksonTester.parseObject(expectedJson).getEnd().toString());
    }

    @Test
    @SneakyThrows
    @DisplayName("Обработка JSON из строки, поле 'end' в прошлом")
    void parsingJsonFromStringInObjectAndEndInPastTest() {
        BookingShortDto dto = BookingShortDto.builder()
                .itemId(1L)
                .start(start)
                .end(end)
                .build();
        String expectedJson = "{"
                + "\"itemId\":1,"
                + "\"start\":\"" + start + "\","
                + "\"end\":\"" + end.minusDays(10) + "\""
                + "}";
        JsonContent<BookingShortDto> jsonContent = bookingShortDtoJacksonTester.write(dto);
        assertThatExceptionOfType(AssertionError.class)
                .isThrownBy(() -> assertThat(jsonContent).isEqualToJson(expectedJson))
                .isInstanceOf(AssertionError.class);
    }

    @Test
    @SneakyThrows
    @DisplayName("Обработка JSON из строки, поле 'start' в прошлом")
    void parsingJsonFromStringInObjectAndStartInPastTest() {
        BookingShortDto dto = BookingShortDto.builder()
                .itemId(1L)
                .start(start)
                .end(end)
                .build();
        String expectedJson = "{"
                + "\"itemId\":1,"
                + "\"start\":\"" + start.minusDays(10) + "\","
                + "\"end\":\"" + end + "\""
                + "}";
        JsonContent<BookingShortDto> jsonContent = bookingShortDtoJacksonTester.write(dto);
        assertThatExceptionOfType(AssertionError.class)
                .isThrownBy(() -> assertThat(jsonContent).isEqualToJson(expectedJson))
                .isInstanceOf(AssertionError.class);
    }

    @Test
    @SneakyThrows
    @DisplayName("Дата окончания в прошлом")
    void bookingShortDtoEndDateInPastTest() {
        String jsonBookingShortDto = jsonBookingShort.put("end", end.minusDays(2)).toString();
        BookingShortDto bookingShortDto = objectMapper.readValue(jsonBookingShortDto, BookingShortDto.class);
        Set<ConstraintViolation<BookingShortDto>> violations = validator.validate(bookingShortDto);

        assertEquals(1, violations.size());
        violations.stream()
                .forEach(violation -> {
                    assertEquals("end", violation.getPropertyPath().toString());
                    assertEquals("{javax.validation.constraints.Future.message}", violation.getMessageTemplate());
                });
    }

    @Test
    @SneakyThrows
    @DisplayName("itemId имеет значение null")
    void bookingShortDtoItemIdIsNullTest() {
        String jsonBookingShortDto = jsonBookingShort.put("itemId", null).toString();

        BookingShortDto bookingShortDto = objectMapper.readValue(jsonBookingShortDto, BookingShortDto.class);
        Set<ConstraintViolation<BookingShortDto>> violations = validator.validate(bookingShortDto);
        assertEquals(1, violations.size());

        violations.stream()
                .forEach(violation -> {
                    assertEquals("itemId", violation.getPropertyPath().toString());
                    assertEquals("{javax.validation.constraints.NotNull.message}", violation.getMessageTemplate());
                });
    }

    @Test
    @SneakyThrows
    @DisplayName("Дата начала в прошлом")
    void bookingShortDtoStartDateInPastTest() {
        String jsonBookingShortDto = jsonBookingShort.put("end", end.minusDays(2)).toString();
        BookingShortDto bookingShortDto = objectMapper.readValue(jsonBookingShortDto, BookingShortDto.class);
        Set<ConstraintViolation<BookingShortDto>> violations = validator.validate(bookingShortDto);
        assertEquals(1, violations.size());
        violations.stream()
                .forEach(violation -> {
                    assertEquals("end", violation.getPropertyPath().toString());
                    assertEquals("{javax.validation.constraints.Future.message}", violation.getMessageTemplate());
                });
    }

}