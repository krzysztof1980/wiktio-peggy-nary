package wiktiopeggynary.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Krzysztof Witukiewicz
 */
public class ServiceLocator {

	private static Map<Class, Object> services = new HashMap<>();

	public static <T> T getService(Class<T> serviceType) {
		return (T) services.get(serviceType);
	}

	public static void loadService(Object service) {
		services.put(service.getClass(), service);
	}
}
