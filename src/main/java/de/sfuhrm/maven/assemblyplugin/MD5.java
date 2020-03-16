package de.sfuhrm.maven.assemblyplugin;

import org.apache.maven.plugins.assembly.filter.ContainerDescriptorHandler;
import org.codehaus.plexus.component.annotations.Component;

@Component(role = ContainerDescriptorHandler.class, hint = "message-digest-md5")
public class MD5 extends MessageDigestContainerDescriptorHandler {
    public MD5() {
        setMessageDigest("MD5");
        setChecksumFile("MD5SUMS");
    }
}
