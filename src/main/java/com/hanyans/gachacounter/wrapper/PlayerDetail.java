package com.hanyans.gachacounter.wrapper;

import java.util.HashMap;
import java.util.NoSuchElementException;


/**
 * Represents the data of a player's details (authkey, etc) extracted from the
 * player's URL.
 */
public class PlayerDetail {
    public final String urlLink;
    public final String authKeyVer;
    public final String signType;
    public final String authAppId;
    public final String winMode;
    public final String gachaId;
    public final String timestamp;
    public final String region;
    public final String lang;
    public final String authKey;
    public final String osSystem;
    public final String deviceModel;
    public final String platformType;
    public final String gameBiz;
    public final String deviceType;
    public final String initType;
    public final String gameVersion;


    private PlayerDetail(
                String urlLink,
                String authKeyVer,
                String signType,
                String authAppId,
                String winMode,
                String gachaId,
                String timestamp,
                String region,
                String lang,
                String authKey,
                String osSystem,
                String deviceModel,
                String platformType,
                String gameBiz,
                String deviceType,
                String initType,
                String gameVersion) {
        this.urlLink = urlLink;
        this.authKeyVer = authKeyVer;
        this.signType = signType;
        this.authAppId = authAppId;
        this.winMode = winMode;
        this.gachaId = gachaId;
        this.timestamp = timestamp;
        this.region = region;
        this.lang = lang;
        this.authKey = authKey;
        this.osSystem = osSystem;
        this.deviceModel = deviceModel;
        this.platformType = platformType;
        this.gameBiz = gameBiz;
        this.deviceType = deviceType;
        this.initType = initType;
        this.gameVersion = gameVersion;
    }


    /**
     * Parses the given {@code urlString} to a {@code PlayerDetail}.
     *
     * @param urlString - the player url String to parse.
     * @throws NoSuchElementException if the {@code urlString} contains missing
     *      elements.
     */
    public static PlayerDetail of(String urlString) throws NoSuchElementException {
        String[] urlParts = urlString.split("\\?", 2);
        if (urlParts.length < 2) {
            throw new NoSuchElementException("URL missing argument part");
        }
        HashMap<String, String> argMap = new HashMap<>();
        for (String arg : urlParts[1].split("&")) {
            String[] parts = arg.split("=", 2);
            String flag = parts[0];
            if (parts.length < 2) {
                throw new NoSuchElementException(String.format("%s missing value", flag));
            }
            String value = parts[1];
            argMap.put(flag, value);
        }
        return formPlayerDetails(urlParts[0], argMap);
    }


    private static PlayerDetail formPlayerDetails(String urlLink, HashMap<String, String> argMap) {
        return new PlayerDetail(
                urlLink,
                argMap.get(HistoryRetriever.AUTH_KEY_VER_FLAG),
                argMap.get(HistoryRetriever.SIGN_TYPE_FLAG),
                argMap.get(HistoryRetriever.AUTH_APPID_FLAG),
                argMap.get(HistoryRetriever.WIN_MODE_FLAG),
                argMap.get(HistoryRetriever.GACHA_ID_FLAG),
                argMap.get(HistoryRetriever.TIMESTAMP_FLAG),
                argMap.get(HistoryRetriever.REGION_FLAG),
                argMap.get(HistoryRetriever.LANG_FLAG),
                argMap.get(HistoryRetriever.AUTH_KEY_FLAG),
                argMap.get(HistoryRetriever.OS_SYSTEM_FLAG),
                argMap.get(HistoryRetriever.DEVICE_MODEL_FLAG),
                argMap.get(HistoryRetriever.PLATFORM_TYPE_FLAG),
                argMap.get(HistoryRetriever.GAME_BIZ_FLAG),
                argMap.get(HistoryRetriever.DEVICE_TYPE_FLAG),
                argMap.get(HistoryRetriever.INIT_TYPE_FLAG),
                argMap.get(HistoryRetriever.GAME_VERSION_FLAG));
    }


    /**
     * Forms a {@link HistoryRetriever} from the data of this
     * {@code PlayerDetail}.
     */
    public HistoryRetriever formHistoryRetriever() {
        return HistoryRetriever.of()
                .setGachaLogUrl(urlLink)
                .setAuthKeyVer(authKeyVer)
                .setSignType(signType)
                .setAuthAppId(authAppId)
                .setWinMode(winMode)
                .setGachaId(gachaId)
                .setTimestamp(timestamp)
                .setRegion(region)
                .setLang(lang)
                .setAuthKey(authKey)
                .setOsSystem(osSystem)
                .setDeviceModel(deviceModel)
                .setPlatformType(platformType)
                .setGameBiz(gameBiz)
                .setDeviceType(deviceType)
                .setInitType(initType)
                .setGameVersion(gameVersion);
    }
}
