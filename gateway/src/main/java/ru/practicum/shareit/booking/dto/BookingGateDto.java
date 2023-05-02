package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
public class BookingGateDto {

    @NotNull
    private long itemId;

    @FutureOrPresent
    @JsonFormat
    @NotNull
    private LocalDateTime start;

    @Future
    @JsonFormat
    @NotNull
    private LocalDateTime end;
}
