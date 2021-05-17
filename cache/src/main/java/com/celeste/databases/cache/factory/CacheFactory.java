package com.celeste.databases.cache.factory;

import com.celeste.databases.cache.model.database.provider.Cache;
import com.celeste.databases.cache.model.database.type.CacheType;
import com.celeste.databases.core.model.database.provider.exception.FailedConnectionException;
import com.celeste.databases.core.model.database.type.AccessType;
import com.celeste.databases.core.model.entity.Credentials;
import java.lang.reflect.Constructor;
import java.util.Properties;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CacheFactory {

  private static final CacheFactory INSTANCE;

  static {
    INSTANCE = new CacheFactory();
  }

  public Cache start(final Properties properties) throws FailedConnectionException {
    try {
      final String driver = properties.getProperty("driver");
      final CacheType cache = CacheType.getCache(driver);

      final AccessType access = cache.getAccess();
      final Credentials credentials = access.serialize(properties);

      final Constructor<? extends Cache> constructor = cache.getProvider()
          .getConstructor(access.getCredentials());

      return constructor.newInstance(credentials);
    } catch (Exception exception) {
      throw new FailedConnectionException(exception.getMessage(), exception.getCause());
    }
  }

  public static CacheFactory getInstance() {
    return INSTANCE;
  }

}