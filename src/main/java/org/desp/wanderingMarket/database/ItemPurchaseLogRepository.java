package org.desp.wanderingMarket.database;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.desp.wanderingMarket.dto.ItemPurchaseLogDto;

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
}
