package de.sfuhrm.maven.assemblyplugin;

import org.apache.maven.plugins.assembly.filter.ContainerDescriptorHandler;
import org.codehaus.plexus.component.annotations.Component;

@Component(role = ContainerDescriptorHandler.class, hint = "message-digest-sha-1")
public class SHA1 extends MessageDigestContainerDescriptorHandler {
    public SHA1() {
        setMessageDigest("SHA");
        setChecksumFile("SHA1SUMS");
    }
}
