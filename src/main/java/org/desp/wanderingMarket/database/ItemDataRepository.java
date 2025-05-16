package org.desp.wanderingMarket.database;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import lombok.Getter;
import org.bson.Document;
import org.desp.wanderingMarket.dto.ItemDataDto;

public class ItemDataRepository {

    private static ItemDataRepository instance;
    private final MongoCollection<Document> itemDataDB;
    @Getter
    public Map<Integer, ItemDataDto> itemDataList = new HashMap<>();
    @Getter
    public List<ItemDataDto> shuffledItemDataList = new ArrayList<>();

    public ItemDataRepository() {
        DatabaseRegister database = new DatabaseRegister();
        this.itemDataDB = database.getDatabase().getCollection("ItemData");
    }

    public static ItemDataRepository getInstance() {
        if (instance == null) {
            instance = new ItemDataRepository();
        }
        return instance;
    }

    public void loadItemData() {
        FindIterable<Document> documents = itemDataDB.find();
        for (Document document : documents) {
            ItemDataDto item = ItemDataDto.builder()
                    .itemID(document.getInteger("itemID"))
                    .MMOItem_ID(document.getString("MMOItem_ID"))
                    .amount(document.getInteger("amount"))
                    .price(document.getInteger("price"))
                    .appearancePercentage(document.getInteger("appearancePercentage"))
                    .userMaxPurchaseAmount(document.getInteger("userMaxPurchaseAmount"))
                    .build();

            itemDataList.put(item.getItemID(), item);
        }
    }

    public void getShuffledRandomItemDataList() {
        int randomSize = ThreadLocalRandom.current().nextInt(3, 9);

        List<ItemDataDto> pool = new ArrayList<>(itemDataList.values());
        List<ItemDataDto> result = new ArrayList<>();

        for (int i = 0; i < randomSize && !pool.isEmpty(); i++) {
            int totalWeight = pool.stream()
                    .mapToInt(ItemDataDto::getAppearancePercentage)
                    .sum();

            int randomWeight = ThreadLocalRandom.current().nextInt(totalWeight);

            int cumulative = 0;
            ItemDataDto selected = null;

            for (ItemDataDto item : pool) {
                cumulative += item.getAppearancePercentage();
                if (randomWeight < cumulative) {
                    selected = item;
                    break;
                }
            }

            if (selected != null) {
                result.add(selected);
                pool.remove(selected);
            }
        }
        shuffledItemDataList = result;
    }
}
