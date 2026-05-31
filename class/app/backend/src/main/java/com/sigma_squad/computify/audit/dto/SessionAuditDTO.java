package com.sigma_squad.computify.audit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SessionAuditDTO {
    private Long id;
    private Long userid;
    private String username;
    private Long computerid;
    private String computernumber;
    private Instant starttime;
    private Instant endtime;
    private Long minutesused;
}
