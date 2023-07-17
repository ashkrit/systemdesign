package org.planetscale.app.archi.level5;

import org.planetscale.app.service.UserRepository;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class CacheRepository implements InvocationHandler {
    private final UserRepository repository;
    private final Set<String> cachedMethods;
    private final Map<String, Object> cacheResult = new LinkedHashMap<>();

    public CacheRepository(UserRepository repository, Set<String> cachedMethods) {
        this.repository = repository;
        this.cachedMethods = cachedMethods;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {


        var key = cacheKey(method, args);
        var cache = cachedMethods.contains(method.getName());
        if (cache) {
            synchronized (cacheResult) {
                if (cacheResult.containsKey(key)) {
                    System.out.println("Hits for " + key);
                    return cacheResult.get(key);
                }
            }
        }

        var result = method.invoke(repository, args);
        cacheIfRequired(key, cache, result);

        return result;
    }

    private void cacheIfRequired(String key, boolean cache, Object result) {
        if (cache) {
            synchronized (cacheResult) {
                cacheResult.put(key, result);
            }
        }
    }

    private static String cacheKey(Method method, Object[] args) {
        var hash = args != null ? Arrays.hashCode(args) : 0;
        return String.format("%s_%s", method.getName(), hash);
    }

    public static UserRepository create(UserRepository target,Set<String> methods) {
        return (UserRepository) Proxy.newProxyInstance(
                CacheRepository.class.getClassLoader(),
                new Class[]{UserRepository.class},
                new CacheRepository(target,methods));
    }
}
