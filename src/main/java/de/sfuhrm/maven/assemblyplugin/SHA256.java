package de.sfuhrm.maven.assemblyplugin;

import org.apache.maven.plugins.assembly.filter.ContainerDescriptorHandler;
import org.codehaus.plexus.component.annotations.Component;

@Component(role = ContainerDescriptorHandler.class, hint = "message-digest-sha-2-256")
public class SHA256 extends MessageDigestContainerDescriptorHandler {
    public SHA256() {
        setMessageDigest("SHA-256");
        setChecksumFile("SHA256SUMS");
    }
}
