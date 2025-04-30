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
import org.desp.sapphireMarket.dto.PlayerDataDto;

public class PlayerDataRepository {

    private static PlayerDataRepository instance;
    private final MongoCollection<Document> playerList;
    private static final Map<String, PlayerDataDto> playerListCache = new HashMap<>();

    public PlayerDataRepository() {
        DatabaseRegister database = new DatabaseRegister();
        this.playerList = database.getDatabase().getCollection("PlayerData");
    }

    public static synchronized PlayerDataRepository getInstance() {
        if (instance == null) {
            instance = new PlayerDataRepository();
        }
        return instance;
    }

    // 플레이어의 데이터 로드 (캐시에도 저장)
    public void loadPlayerData(Player player) {
        String user_id = player.getName();
        String uuid = player.getUniqueId().toString();

        Document document = new Document("uuid", uuid);
        if (playerList.find(Filters.eq("uuid", uuid)).first() == null) {
            // 새로운 유저일 경우 DB에 삽입
            Document newUserDocument = new Document()
                    .append("user_id", user_id)
                    .append("uuid", uuid)
                    .append("sapphireAmount", 0);
            playerList.insertOne(newUserDocument);
        }

        // DB에서 사파이어 양 가져오기
        int sapphireAmount = playerList.find(document).first().getInteger("sapphireAmount");
        PlayerDataDto playerDto = PlayerDataDto.builder()
                .user_id(user_id)
                .uuid(uuid)
                .sapphireAmount(sapphireAmount)
                .build();
        // 캐시에 저장
        playerListCache.put(uuid, playerDto);
    }

    public void loadAllPlayerData() {
        FindIterable<Document> documents = playerList.find();
        for (Document document : documents) {
            String user_id = document.getString("user_id");
            String uuid = document.getString("uuid");
            int sapphireAmount = document.getInteger("sapphireAmount");

            PlayerDataDto playerDto = PlayerDataDto.builder()
                    .user_id(user_id)
                    .uuid(uuid)
                    .sapphireAmount(sapphireAmount)
                    .build();

            playerListCache.put(uuid, playerDto);
        }
    }

    // 플레이어 이름으로 UUID 얻기
    public String getPlayerUUID(String playerName) {
        Document document = playerList.find(Filters.eq("user_id", playerName)).first();
        if (document != null) {
            return document.getString("uuid");
        }
        return null;
    }

    // 플레이어 데이터 가져오기 (온라인 플레이어는 캐시, 오프라인 플레이어는 DB에서 처리)
    public PlayerDataDto getPlayerData(Player player) {
        if (player.isOnline()) {
            return playerListCache.get(player.getUniqueId().toString()); // 온라인 플레이어는 캐시에서 가져옴
        } else {
            // 오프라인 플레이어는 DB에서 가져옴
            String uuid = player.getUniqueId().toString();
            Document document = playerList.find(Filters.eq("uuid", uuid)).first();
            if (document != null) {
                String user_id = document.getString("user_id");
                int sapphireAmount = document.getInteger("sapphireAmount");
                return PlayerDataDto.builder()
                        .user_id(user_id)
                        .uuid(uuid)
                        .sapphireAmount(sapphireAmount)
                        .build();
            }
        }
        return null;  // 데이터가 없으면 null 반환
    }

    // 사파이어 지급 (온라인 플레이어는 캐시에서 수정, 오프라인은 DB에서 수정)
    public void addSapphireAmount(String playerUUID, int sapphireAmount) {
        PlayerDataDto playerDataDto = playerListCache.get(playerUUID);
        if (playerDataDto != null) {
            // 온라인 플레이어인 경우 캐시에서 처리
            playerDataDto.setSapphireAmount(playerDataDto.getSapphireAmount() + sapphireAmount);
            playerListCache.put(playerDataDto.getUuid(), playerDataDto);
        } else {
            // 오프라인 플레이어는 DB에서 처리
            Document document = playerList.find(Filters.eq("uuid", playerUUID)).first();
            if (document != null) {
                int currentAmount = document.getInteger("sapphireAmount");
                document.put("sapphireAmount", currentAmount + sapphireAmount);
                playerList.replaceOne(Filters.eq("uuid", playerDataDto.getUuid()), document);
            }
        }
    }

    // 사파이어 차감 (온라인 플레이어는 캐시에서 수정, 오프라인은 DB에서 수정)
    public void reduceSapphireAmount(String playerUUID, int sapphireAmount) {
        PlayerDataDto playerDataDto = playerListCache.get(playerUUID);
        if (playerDataDto != null) {
            // 온라인 플레이어인 경우 캐시에서 처리
            if (playerDataDto.getSapphireAmount() < sapphireAmount) {
                playerDataDto.setSapphireAmount(0);
            } else {
                playerDataDto.setSapphireAmount(playerDataDto.getSapphireAmount() - sapphireAmount);
            }
            playerListCache.put(playerUUID, playerDataDto);
        } else {
            // 오프라인 플레이어는 DB에서 처리
            Document document = playerList.find(Filters.eq("uuid", playerUUID)).first();
            if (document != null) {
                int currentAmount = document.getInteger("sapphireAmount");
                if (currentAmount < sapphireAmount) {
                    document.put("sapphireAmount", 0);
                } else {
                    document.put("sapphireAmount", currentAmount - sapphireAmount);
                }
                playerList.replaceOne(Filters.eq("uuid", playerUUID), document);
            }
        }
    }

    // 플레이어 데이터 저장 (캐시에서 DB로)
    public void savePlayerData(Player player) {
        String user_id = player.getName();
        String uuid = player.getUniqueId().toString();
        int sapphireAmount = playerListCache.get(uuid).getSapphireAmount();

        Document document = new Document()
                .append("user_id", user_id)
                .append("uuid", uuid)
                .append("sapphireAmount", sapphireAmount);

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
            int sapphireAmount = playerDataDto.getSapphireAmount();
            Document document = new Document()
                    .append("user_id", user_id)
                    .append("uuid", uuid)
                    .append("sapphireAmount", sapphireAmount);

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
