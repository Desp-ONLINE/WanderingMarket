package org.desp.wanderingMarket.database;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bukkit.entity.Player;
import org.desp.wanderingMarket.dto.ItemPurchaseLogDto;

public class ItemPurchaseMemoryLogRepository {

    private static ItemPurchaseMemoryLogRepository instance;
    private final MongoCollection<Document> itemPurchaseDB;

    public ItemPurchaseMemoryLogRepository() {
        DatabaseRegister database = new DatabaseRegister();
        this.itemPurchaseDB = database.getDatabase().getCollection("ItemPurchaseMemoryLog");
    }

    public static ItemPurchaseMemoryLogRepository getInstance() {
        if (instance == null) {
            instance = new ItemPurchaseMemoryLogRepository();
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

    public void resetPurchaseMemoryLog() {
        itemPurchaseDB.deleteMany(new Document());
    }

    public int countPurchaseLog(Player player, int itemID) {
        long count = itemPurchaseDB.countDocuments(
                Filters.and(
                        Filters.eq("uuid", player.getUniqueId().toString()),
                        Filters.eq("purchaseItemID", itemID)
                )
        );

        return (int) count;
    }
}
