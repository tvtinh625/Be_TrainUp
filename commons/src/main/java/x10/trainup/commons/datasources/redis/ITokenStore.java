package x10.trainup.commons.datasources.redis;

public interface ITokenStore {
    void saveToken(String key, String token, long ttlMillis);
    String getToken(String key);
    void deleteToken(String key);
}
