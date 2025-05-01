package org.desp.wanderingMarket.database;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.bson.Document;
import org.bukkit.entity.Player;
import org.desp.wanderingMarket.dto.PlayerDataDto;

public class PlayerDataRepository {

    private static PlayerDataRepository instance;
    private final MongoCollection<Document> playerList;
    private static final Map<String, PlayerDataDto> playerListCache = new HashMap<>();

    public PlayerDataRepository() {
        DatabaseRegister database = new DatabaseRegister();
        this.playerList = database.getDatabase().getCollection("PlayerData");
    }

    public static PlayerDataRepository getInstance() {
        if (instance == null) {
            instance = new PlayerDataRepository();
        }
        return instance;
    }

    // 플레이어의 데이터 로드 (캐시에도 저장)
    public void loadPlayerData(Player player) {
        String user_id = player.getName();
        String uuid = player.getUniqueId().toString();

        if (playerList.find(Filters.eq("uuid", uuid)).first() == null) {
            // 새로운 유저일 경우 DB에 삽입
            Document newUserDocument = new Document()
                    .append("user_id", user_id)
                    .append("uuid", uuid);
            playerList.insertOne(newUserDocument);
        }

        PlayerDataDto playerDto = PlayerDataDto.builder()
                .user_id(user_id)
                .uuid(uuid)
                .build();
        // 캐시에 저장
        playerListCache.put(uuid, playerDto);
    }

    public void loadAllPlayerData() {
        FindIterable<Document> documents = playerList.find();
        for (Document document : documents) {
            String user_id = document.getString("user_id");
            String uuid = document.getString("uuid");

            PlayerDataDto playerDto = PlayerDataDto.builder()
                    .user_id(user_id)
                    .uuid(uuid)
                    .build();

            playerListCache.put(uuid, playerDto);
        }
    }

    // 플레이어 데이터 저장 (캐시에서 DB로)
    public void savePlayerData(Player player) {
        String user_id = player.getName();
        String uuid = player.getUniqueId().toString();

        Document document = new Document()
                .append("user_id", user_id)
                .append("uuid", uuid);

        playerList.replaceOne(
                Filters.eq("uuid", uuid),
                document,
                new ReplaceOptions().upsert(true)
        );
    }

    public void saveAllPlayerData() {
        for (Entry<String, PlayerDataDto> stringPlayerDataDtoEntry : playerListCache.entrySet()) {
            PlayerDataDto playerDataDto = stringPlayerDataDtoEntry.getValue();

            String user_id = playerDataDto.getUser_id();
            String uuid = playerDataDto.getUuid();
            Document document = new Document()
                    .append("user_id", user_id)
                    .append("uuid", uuid);

            playerList.replaceOne(
                    Filters.eq("uuid", uuid),
                    document,
                    new ReplaceOptions().upsert(true)
            );
        }
    }

    // 캐시의 모든 플레이어 데이터 가져오기
    public Map<String, PlayerDataDto> getPlayerListCache() {
        return playerListCache;
    }
}
