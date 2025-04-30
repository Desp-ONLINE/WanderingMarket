package org.desp.wanderingMarket.database;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import java.util.Arrays;
import org.bson.Document;
import org.bukkit.entity.Player;
import org.desp.sapphireMarket.dto.ItemPurchaseLogDto;
import org.desp.sapphireMarket.utils.DateUtil;

public class ItemPurchaseLogRepository {

    private static ItemPurchaseLogRepository instance;
    private final MongoCollection<Document> itemPurchaseDB;

    public ItemPurchaseLogRepository() {
        DatabaseRegister database = new DatabaseRegister();
        this.itemPurchaseDB = database.getDatabase().getCollection("ItemPurchaseLog");
    }

    public static ItemPurchaseLogRepository getInstance() {
        if (instance == null) {
            instance = new ItemPurchaseLogRepository();
        }
        return instance;
    }

    public void insertPurchaseLog(ItemPurchaseLogDto dto) {

        Document document = new Document()
                .append("user_id", dto.getUser_id())
                .append("uuid", dto.getUuid())
                .append("purchaseItemID", dto.getPurchaseItemID())
                .append("amount", dto.getAmount())
                .append("purchasePrice", dto.getPurchasePrice())
                .append("purchaseTime", dto.getPurchaseTime());

        itemPurchaseDB.insertOne(document);
    }

    public int countPurchaseLog(Player player, String MMOItem_ID) {
        AggregateIterable<Document> results = itemPurchaseDB.aggregate(Arrays.asList(
                Aggregates.match(Filters.and(
                        Filters.eq("uuid", player.getUniqueId().toString()),
                        Filters.eq("purchaseItemID", MMOItem_ID)
                )),
                Aggregates.group(null, Accumulators.sum("totalAmount", "$amount"))
        ));

        Document result = results.first();
        if (result != null) {
            int totalAmount = result.getInteger("totalAmount");
            return totalAmount;
        } else {
            return 0;
        }
    }

    public int countTodayPurchaseLog(Player player, String MMOItem_ID) {
        String todayDate = DateUtil.getCurrentDate(); // 예: "2025-03-20"

        AggregateIterable<Document> results = itemPurchaseDB.aggregate(Arrays.asList(
                Aggregates.match(Filters.and(
                        Filters.eq("uuid", player.getUniqueId().toString()),
                        Filters.eq("purchaseItemID", MMOItem_ID),
                        Filters.regex("purchaseTime", "^" + todayDate)
                )),
                Aggregates.group(null, Accumulators.sum("totalAmount", "$amount"))
        ));

        Document result = results.first();
        if (result != null) {
            return result.getInteger("totalAmount");
        } else {
            return 0;
        }
    }

//    public int countServerPurchaseLog(String MMOItem_ID) {
//        AggregateIterable<Document> results = itemPurchaseDB.aggregate(Arrays.asList(
//                Aggregates.match(Filters.eq("purchaseItemID", MMOItem_ID)),
//                Aggregates.group(null, Accumulators.sum("totalAmount", "$amount"))
//        ));
//
//        Document result = results.first();
//        if (result != null) {
//            return result.getInteger("totalAmount");
//        } else {
//            return 0;
//        }
//    }
}
