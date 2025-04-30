package org.desp.wanderingMarket.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class ItemPurchaseLogDto {
    private String user_id;
    private String uuid;
    private String purchaseItemID;
    private int amount;
    private int purchasePrice;
    private String purchaseTime;
}

