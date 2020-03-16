package de.sfuhrm.maven.assemblyplugin;

import org.junit.jupiter.api.Test;

import java.security.MessageDigest;
import java.security.Provider;
import java.security.Security;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/** Not a real test. Just outputs the message digest algorithms the current JDK is able
 * to provide.
 * */
public class ListMessageDigestAlgorithmsTest {

    @Test
    public void listMessageDigestAlgorithms() {
        final String type = MessageDigest.class.getSimpleName();

        final List<Provider.Service> services = Arrays.asList(Security.getProviders())
                .stream()
                .flatMap(provider -> provider.getServices().stream())
                .filter(service -> service.getType().equalsIgnoreCase(type))
                .sorted((o1, o2) -> o1.getAlgorithm().compareToIgnoreCase(o2.getAlgorithm()))
                .collect(Collectors.toList());
        services.forEach(s -> System.out.println(s.getAlgorithm()));
    }
}
