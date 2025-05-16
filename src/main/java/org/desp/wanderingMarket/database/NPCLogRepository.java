package org.desp.wanderingMarket.database;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.desp.wanderingMarket.dto.NPCLogDto;

public class NPCLogRepository {

    private static NPCLogRepository instance;
    private final MongoCollection<Document> npcLogDB;

    public NPCLogRepository() {
        DatabaseRegister database = new DatabaseRegister();
        this.npcLogDB = database.getDatabase().getCollection("NPCLog");
    }

    public static NPCLogRepository getInstance() {
        if (instance == null) {
            instance = new NPCLogRepository();
        }
        return instance;
    }

    public void insertNPCLog(NPCLogDto dto) {
        Document document = new Document()
                .append("date", dto.getDate())
                .append("currentTime", dto.getCurrentTime())
                .append("nextTIme", dto.getNextTime())
                .append("village", dto.getVillage());

        npcLogDB.insertOne(document);
    }
}
