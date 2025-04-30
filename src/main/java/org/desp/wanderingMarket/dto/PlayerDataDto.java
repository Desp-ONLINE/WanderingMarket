package org.desp.wanderingMarket.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class PlayerDataDto {
    private String user_id;
    private String uuid;
    private int sapphireAmount;
}
