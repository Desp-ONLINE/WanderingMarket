package org.desp.wanderingMarket.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class NPCLocationDto {
    private int npcID;
    private String location;
    private double x;
    private double y;
    private double z;
    private double yaw;
    private double pitch;
}
