package org.desp.wanderingMarket.database;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import org.bson.Document;
import org.desp.wanderingMarket.dto.ItemDataDto;
import org.desp.wanderingMarket.dto.NPCLocationDto;

public class NPCLocationDataRepository {

    private static NPCLocationDataRepository instance;
    private final MongoCollection<Document> locationList;
    @Getter
    private final Map<Integer, NPCLocationDto> npcLocationMap = new HashMap<>();
    @Getter
    private List<NPCLocationDto> npcLocationDtoList = new ArrayList<>();

    public NPCLocationDataRepository() {
        DatabaseRegister database = new DatabaseRegister();
        this.locationList = database.getDatabase().getCollection("NPCLocation");
    }

    public static NPCLocationDataRepository getInstance() {
        if (instance == null) {
            instance = new NPCLocationDataRepository();
        }
        return instance;
    }

    public void loadAllPlayerData() {
        FindIterable<Document> documents = locationList.find();
        for (Document document : documents) {
            NPCLocationDto locationDto = NPCLocationDto.builder()
                    .npcID(document.getInteger("npcID"))
                    .location(document.getString("location"))
                    .x(document.getDouble("x"))
                    .y(document.getDouble("y"))
                    .z(document.getDouble("z"))
                    .yaw(document.getDouble("yaw"))
                    .pitch(document.getDouble("pitch"))
                    .build();

            npcLocationMap.put(locationDto.getNpcID(), locationDto);
        }
    }

    public NPCLocationDto getRandomNpcLocationDto() {
        npcLocationDtoList = new ArrayList<>(npcLocationMap.values());
        Collections.shuffle(npcLocationDtoList); // 무작위 섞기
        return npcLocationDtoList.getFirst();
    }
}
