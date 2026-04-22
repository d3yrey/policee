package com.example.policee.dto.response;

import com.example.policee.util.config.enums.Status;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class OtpResDto {
    private Status status;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDateTime blockTime;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDateTime loginTime;
}