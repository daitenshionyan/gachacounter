package com.hanyans.gachacounter.wrapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Optional;


/**
 * Utility class to form the URL string to retrieve gacha logs from server.
 */
public class HistoryRetriever {
    public static final String AUTH_KEY_VER_FLAG = "authkey_ver";
    public static final String SIGN_TYPE_FLAG = "sign_type";
    public static final String AUTH_APPID_FLAG = "auth_appid";
    public static final String WIN_MODE_FLAG = "win_mode";
    public static final String GACHA_ID_FLAG = "gacha_id";
    public static final String TIMESTAMP_FLAG = "timestamp";
    public static final String REGION_FLAG = "region";
    public static final String DEFAULT_GACHA_TYPE_FLAG = "default_gacha_type";
    public static final String LANG_FLAG = "lang";
    public static final String AUTH_KEY_FLAG = "authkey";
    public static final String OS_SYSTEM_FLAG = "os_system";
    public static final String DEVICE_MODEL_FLAG = "device_model";
    public static final String PLATFORM_TYPE_FLAG = "plat_type";
    public static final String GAME_BIZ_FLAG = "game_biz";
    public static final String PAGE_FLAG = "page";
    public static final String SIZE_FLAG = "size";
    public static final String GACHA_TYPE_FLAG = "gacha_type";
    public static final String END_ID_FLAG = "end_id";
    public static final String START_ID_FLAG = "begin_id";

    public static final String DEVICE_TYPE_FLAG = "device_type";
    public static final String INIT_TYPE_FLAG = "init_type";
    public static final String GAME_VERSION_FLAG = "game_version";


    private final Optional<String> gachaLogUrl;
    private final Optional<String> authKeyVer;
    private final Optional<String> signType;
    private final Optional<String> authAppId;
    private final Optional<String> winMode;
    private final Optional<String> gachaId;
    private final Optional<String> timestamp;
    private final Optional<String> region;
    private final Optional<String> defaultGachaType;
    private final Optional<String> lang;
    private final Optional<String> authKey;
    private final Optional<String> osSystem;
    private final Optional<String> deviceModel;
    private final Optional<String> platformType;
    private final Optional<String> gameBiz;
    private final Optional<String> page;
    private final Optional<String> size;
    private final Optional<String> gachaType;
    private final Optional<String> endId;
    private final Optional<String> deviceType;
    private final Optional<String> initType;
    private final Optional<String> gameVersion;


    private HistoryRetriever(
                Optional<String> gachaLogUrl,
                Optional<String> authKeyVer,
                Optional<String> signType,
                Optional<String> authAppId,
                Optional<String> winMode,
                Optional<String> gachaId,
                Optional<String> timestamp,
                Optional<String> region,
                Optional<String> defaultGachaType,
                Optional<String> lang,
                Optional<String> authKey,
                Optional<String> osSystem,
                Optional<String> deviceModel,
                Optional<String> platformType,
                Optional<String> gameBiz,
                Optional<String> page,
                Optional<String> size,
                Optional<String> gachaType,
                Optional<String> endId,
                Optional<String> deviceType,
                Optional<String> initType,
                Optional<String> gameVersion) {
        this.gachaLogUrl = gachaLogUrl;
        this.authKeyVer = authKeyVer;
        this.signType = signType;
        this.authAppId = authAppId;
        this.winMode = winMode;
        this.gachaId = gachaId;
        this.timestamp = timestamp;
        this.region = region;
        this.defaultGachaType = defaultGachaType;
        this.lang = lang;
        this.authKey = authKey;
        this.osSystem = osSystem;
        this.deviceModel = deviceModel;
        this.platformType = platformType;
        this.gameBiz = gameBiz;
        this.page = page;
        this.size = size;
        this.gachaType = gachaType;
        this.endId = endId;
        this.deviceType = deviceType;
        this.initType = initType;
        this.gameVersion = gameVersion;
    }


    public static HistoryRetriever of() {
        return new HistoryRetriever(
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty());
    }


    public HistoryRetriever setGachaLogUrl(String value) {
        Optional<String> gachaLogUrl = Optional.ofNullable(value);
        return new HistoryRetriever(
                gachaLogUrl,
                authKeyVer,
                signType,
                authAppId,
                winMode,
                gachaId,
                timestamp,
                region,
                defaultGachaType,
                lang,
                authKey,
                osSystem,
                deviceModel,
                platformType,
                gameBiz,
                page,
                size,
                gachaType,
                endId,
                deviceType,
                initType,
                gameVersion);
    }


    public HistoryRetriever setAuthKeyVer(String value) {
        Optional<String> authKeyVer = Optional.ofNullable(value);
        return new HistoryRetriever(
                gachaLogUrl,
                authKeyVer,
                signType,
                authAppId,
                winMode,
                gachaId,
                timestamp,
                region,
                defaultGachaType,
                lang,
                authKey,
                osSystem,
                deviceModel,
                platformType,
                gameBiz,
                page,
                size,
                gachaType,
                endId,
                deviceType,
                initType,
                gameVersion);
    }


    public HistoryRetriever setSignType(String value) {
        Optional<String> signType = Optional.ofNullable(value);
        return new HistoryRetriever(
                gachaLogUrl,
                authKeyVer,
                signType,
                authAppId,
                winMode,
                gachaId,
                timestamp,
                region,
                defaultGachaType,
                lang,
                authKey,
                osSystem,
                deviceModel,
                platformType,
                gameBiz,
                page,
                size,
                gachaType,
                endId,
                deviceType,
                initType,
                gameVersion);
    }


    public HistoryRetriever setAuthAppId(String value) {
        Optional<String> authAppId = Optional.ofNullable(value);
        return new HistoryRetriever(
                gachaLogUrl,
                authKeyVer,
                signType,
                authAppId,
                winMode,
                gachaId,
                timestamp,
                region,
                defaultGachaType,
                lang,
                authKey,
                osSystem,
                deviceModel,
                platformType,
                gameBiz,
                page,
                size,
                gachaType,
                endId,
                deviceType,
                initType,
                gameVersion);
    }


    public HistoryRetriever setWinMode(String value) {
        Optional<String> winMode = Optional.ofNullable(value);
        return new HistoryRetriever(
                gachaLogUrl,
                authKeyVer,
                signType,
                authAppId,
                winMode,
                gachaId,
                timestamp,
                region,
                defaultGachaType,
                lang,
                authKey,
                osSystem,
                deviceModel,
                platformType,
                gameBiz,
                page,
                size,
                gachaType,
                endId,
                deviceType,
                initType,
                gameVersion);
    }


    public HistoryRetriever setGachaId(String value) {
        Optional<String> gachaId = Optional.ofNullable(value);
        return new HistoryRetriever(
                gachaLogUrl,
                authKeyVer,
                signType,
                authAppId,
                winMode,
                gachaId,
                timestamp,
                region,
                defaultGachaType,
                lang,
                authKey,
                osSystem,
                deviceModel,
                platformType,
                gameBiz,
                page,
                size,
                gachaType,
                endId,
                deviceType,
                initType,
                gameVersion);
    }


    public HistoryRetriever setTimestamp(String value) {
        Optional<String> timestamp = Optional.ofNullable(value);
        return new HistoryRetriever(
                gachaLogUrl,
                authKeyVer,
                signType,
                authAppId,
                winMode,
                gachaId,
                timestamp,
                region,
                defaultGachaType,
                lang,
                authKey,
                osSystem,
                deviceModel,
                platformType,
                gameBiz,
                page,
                size,
                gachaType,
                endId,
                deviceType,
                initType,
                gameVersion);
    }


    public HistoryRetriever setRegion(String value) {
        Optional<String> region = Optional.ofNullable(value);
        return new HistoryRetriever(
                gachaLogUrl,
                authKeyVer,
                signType,
                authAppId,
                winMode,
                gachaId,
                timestamp,
                region,
                defaultGachaType,
                lang,
                authKey,
                osSystem,
                deviceModel,
                platformType,
                gameBiz,
                page,
                size,
                gachaType,
                endId,
                deviceType,
                initType,
                gameVersion);
    }


    public HistoryRetriever setDefaultGachaType(String value) {
        Optional<String> defaultGachaType = Optional.ofNullable(value);
        return new HistoryRetriever(
                gachaLogUrl,
                authKeyVer,
                signType,
                authAppId,
                winMode,
                gachaId,
                timestamp,
                region,
                defaultGachaType,
                lang,
                authKey,
                osSystem,
                deviceModel,
                platformType,
                gameBiz,
                page,
                size,
                gachaType,
                endId,
                deviceType,
                initType,
                gameVersion);
    }


    public HistoryRetriever setLang(String value) {
        Optional<String> lang = Optional.ofNullable(value);
        return new HistoryRetriever(
                gachaLogUrl,
                authKeyVer,
                signType,
                authAppId,
                winMode,
                gachaId,
                timestamp,
                region,
                defaultGachaType,
                lang,
                authKey,
                osSystem,
                deviceModel,
                platformType,
                gameBiz,
                page,
                size,
                gachaType,
                endId,
                deviceType,
                initType,
                gameVersion);
    }


    public HistoryRetriever setAuthKey(String value) {
        Optional<String> authKey = Optional.ofNullable(value);
        return new HistoryRetriever(
                gachaLogUrl,
                authKeyVer,
                signType,
                authAppId,
                winMode,
                gachaId,
                timestamp,
                region,
                defaultGachaType,
                lang,
                authKey,
                osSystem,
                deviceModel,
                platformType,
                gameBiz,
                page,
                size,
                gachaType,
                endId,
                deviceType,
                initType,
                gameVersion);
    }


    public HistoryRetriever setOsSystem(String value) {
        Optional<String> osSystem = Optional.ofNullable(value);
        return new HistoryRetriever(
                gachaLogUrl,
                authKeyVer,
                signType,
                authAppId,
                winMode,
                gachaId,
                timestamp,
                region,
                defaultGachaType,
                lang,
                authKey,
                osSystem,
                deviceModel,
                platformType,
                gameBiz,
                page,
                size,
                gachaType,
                endId,
                deviceType,
                initType,
                gameVersion);
    }


    public HistoryRetriever setDeviceModel(String value) {
        Optional<String> deviceModel = Optional.ofNullable(value);
        return new HistoryRetriever(
                gachaLogUrl,
                authKeyVer,
                signType,
                authAppId,
                winMode,
                gachaId,
                timestamp,
                region,
                defaultGachaType,
                lang,
                authKey,
                osSystem,
                deviceModel,
                platformType,
                gameBiz,
                page,
                size,
                gachaType,
                endId,
                deviceType,
                initType,
                gameVersion);
    }


    public HistoryRetriever setPlatformType(String value) {
        Optional<String> platformType = Optional.ofNullable(value);
        return new HistoryRetriever(
                gachaLogUrl,
                authKeyVer,
                signType,
                authAppId,
                winMode,
                gachaId,
                timestamp,
                region,
                defaultGachaType,
                lang,
                authKey,
                osSystem,
                deviceModel,
                platformType,
                gameBiz,
                page,
                size,
                gachaType,
                endId,
                deviceType,
                initType,
                gameVersion);
    }


    public HistoryRetriever setGameBiz(String value) {
        Optional<String> gameBiz = Optional.ofNullable(value);
        return new HistoryRetriever(
                gachaLogUrl,
                authKeyVer,
                signType,
                authAppId,
                winMode,
                gachaId,
                timestamp,
                region,
                defaultGachaType,
                lang,
                authKey,
                osSystem,
                deviceModel,
                platformType,
                gameBiz,
                page,
                size,
                gachaType,
                endId,
                deviceType,
                initType,
                gameVersion);
    }


    public HistoryRetriever setPage(String value) {
        Optional<String> page = Optional.ofNullable(value);
        return new HistoryRetriever(
                gachaLogUrl,
                authKeyVer,
                signType,
                authAppId,
                winMode,
                gachaId,
                timestamp,
                region,
                defaultGachaType,
                lang,
                authKey,
                osSystem,
                deviceModel,
                platformType,
                gameBiz,
                page,
                size,
                gachaType,
                endId,
                deviceType,
                initType,
                gameVersion);
    }


    public HistoryRetriever setPage(int value) {
        return setPage(String.valueOf(value));
    }


    public HistoryRetriever setSize(String value) {
        Optional<String> size = Optional.ofNullable(value);
        return new HistoryRetriever(
                gachaLogUrl,
                authKeyVer,
                signType,
                authAppId,
                winMode,
                gachaId,
                timestamp,
                region,
                defaultGachaType,
                lang,
                authKey,
                osSystem,
                deviceModel,
                platformType,
                gameBiz,
                page,
                size,
                gachaType,
                endId,
                deviceType,
                initType,
                gameVersion);
    }


    public HistoryRetriever setSize(int value) {
        return setSize(String.valueOf(value));
    }


    public HistoryRetriever setGachaType(String value) {
        Optional<String> gachaType = Optional.ofNullable(value);
        return new HistoryRetriever(
                gachaLogUrl,
                authKeyVer,
                signType,
                authAppId,
                winMode,
                gachaId,
                timestamp,
                region,
                defaultGachaType,
                lang,
                authKey,
                osSystem,
                deviceModel,
                platformType,
                gameBiz,
                page,
                size,
                gachaType,
                endId,
                deviceType,
                initType,
                gameVersion);
    }


    public HistoryRetriever setGachaType(GachaType value, Game game) {
        switch (game) {
            case HSR:
                return setGachaType(String.valueOf(value.getTypeIdHsr()));
            case Genshin:
                return setGachaType(String.valueOf(value.getTypeIdGenshin()));
            default:
                throw new IllegalArgumentException(String.format("Unknown game type <%s>", game));
        }
    }


    public HistoryRetriever setEndId(String value) {
        Optional<String> endId = Optional.ofNullable(value);
        return new HistoryRetriever(
                gachaLogUrl,
                authKeyVer,
                signType,
                authAppId,
                winMode,
                gachaId,
                timestamp,
                region,
                defaultGachaType,
                lang,
                authKey,
                osSystem,
                deviceModel,
                platformType,
                gameBiz,
                page,
                size,
                gachaType,
                endId,
                deviceType,
                initType,
                gameVersion);
    }


    public HistoryRetriever setEndId(long value) {
        return setEndId(String.valueOf(value));
    }


    public HistoryRetriever setDeviceType(String value) {
        Optional<String> deviceType = Optional.ofNullable(value);
        return new HistoryRetriever(
                gachaLogUrl,
                authKeyVer,
                signType,
                authAppId,
                winMode,
                gachaId,
                timestamp,
                region,
                defaultGachaType,
                lang,
                authKey,
                osSystem,
                deviceModel,
                platformType,
                gameBiz,
                page,
                size,
                gachaType,
                endId,
                deviceType,
                initType,
                gameVersion);
    }


    public HistoryRetriever setInitType(String value) {
        Optional<String> initType = Optional.ofNullable(value);
        return new HistoryRetriever(
                gachaLogUrl,
                authKeyVer,
                signType,
                authAppId,
                winMode,
                gachaId,
                timestamp,
                region,
                defaultGachaType,
                lang,
                authKey,
                osSystem,
                deviceModel,
                platformType,
                gameBiz,
                page,
                size,
                gachaType,
                endId,
                deviceType,
                initType,
                gameVersion);
    }


    public HistoryRetriever setGameVersion(String value) {
        Optional<String> gameVersion = Optional.ofNullable(value);
        return new HistoryRetriever(
                gachaLogUrl,
                authKeyVer,
                signType,
                authAppId,
                winMode,
                gachaId,
                timestamp,
                region,
                defaultGachaType,
                lang,
                authKey,
                osSystem,
                deviceModel,
                platformType,
                gameBiz,
                page,
                size,
                gachaType,
                endId,
                deviceType,
                initType,
                gameVersion);
    }


    public String formUrlStringHsr() {
        return String.join("",
                gachaLogUrl.orElseThrow(),
                formArg(AUTH_KEY_VER_FLAG, authKeyVer, "?"),
                formArg(SIGN_TYPE_FLAG, signType),
                formArg(AUTH_APPID_FLAG, authAppId),
                formArg(WIN_MODE_FLAG, winMode),
                formArg(GACHA_ID_FLAG, gachaId),
                formArg(TIMESTAMP_FLAG, timestamp),
                formArg(REGION_FLAG, region),
                formArg(LANG_FLAG, lang),
                formArg(DEFAULT_GACHA_TYPE_FLAG, gachaType),
                formArg(AUTH_KEY_FLAG, authKey),
                formArg(OS_SYSTEM_FLAG, osSystem),
                formArg(DEVICE_MODEL_FLAG, deviceModel),
                formArg(PLATFORM_TYPE_FLAG, platformType),
                formArg(GAME_BIZ_FLAG, gameBiz),
                formArg(PAGE_FLAG, page),
                formArg(SIZE_FLAG, size),
                formArg(GACHA_TYPE_FLAG, gachaType),
                formArg(END_ID_FLAG, endId)
        );
    }


    public String formUrlStringGenshin() {
        return String.join("",
                gachaLogUrl.orElseThrow(),
                formArg(WIN_MODE_FLAG, winMode, "?"),
                formArg(AUTH_KEY_VER_FLAG, authKeyVer),
                formArg(SIGN_TYPE_FLAG, signType),
                formArg(AUTH_APPID_FLAG, authAppId),
                formArg(INIT_TYPE_FLAG, initType),
                formArg(GACHA_ID_FLAG, gachaId),
                formArg(TIMESTAMP_FLAG, timestamp),
                formArg(LANG_FLAG, lang),
                formArg(DEVICE_TYPE_FLAG, deviceType),
                formArg(GAME_VERSION_FLAG, gameVersion),
                formArg(PLATFORM_TYPE_FLAG, platformType),
                formArg(REGION_FLAG, region),
                formArg(AUTH_KEY_FLAG, authKey),
                formArg(GAME_BIZ_FLAG, gameBiz),
                formArg(GACHA_TYPE_FLAG, gachaType),
                formArg(PAGE_FLAG, page),
                formArg(SIZE_FLAG, size),
                formArg(END_ID_FLAG, endId)
        );
    }


    public BufferedReader getHistoryReader() {
        String urlString = formUrlStringHsr();
        try {
            URL url = new URL(urlString);
            return new BufferedReader(new InputStreamReader(url.openStream()));
        } catch (Throwable ex) {
            ex.printStackTrace();
            return null;
        }
    }


    private String formArg(String flag, Optional<String> opArg) {
        return formArg(flag, opArg, "&");
    }


    private String formArg(String flag, Optional<String> opArg, String separator) {
        return opArg.map(value -> String.format("%s%s=%s", separator, flag, value))
                .orElse("");
    }
}
