package org.desp.wanderingMarket.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class ItemDataDto {
    private int itemID;
    private String MMOItem_ID;
    private int amount;
    private int price;
    private int appearancePercentage;
    private int userMaxPurchaseAmount;
}
