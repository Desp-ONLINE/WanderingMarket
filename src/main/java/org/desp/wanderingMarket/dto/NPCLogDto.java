package org.desp.wanderingMarket.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class NPCLogDto {
    private String date;
    private String currentTime;
    private String nextTime;
    private String village;
}
