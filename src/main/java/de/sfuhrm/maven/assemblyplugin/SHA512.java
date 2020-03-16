package de.sfuhrm.maven.assemblyplugin;

import org.apache.maven.plugins.assembly.filter.ContainerDescriptorHandler;
import org.codehaus.plexus.component.annotations.Component;

@Component(role = ContainerDescriptorHandler.class, hint = "message-digest-sha-2-512")
public class SHA512 extends MessageDigestContainerDescriptorHandler {
    public SHA512() {
        setMessageDigest("SHA-512");
        setChecksumFile("SHA512SUMS");
    }
}
