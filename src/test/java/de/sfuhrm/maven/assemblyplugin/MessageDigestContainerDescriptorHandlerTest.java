package de.sfuhrm.maven.assemblyplugin;

public class MessageDigestContainerDescriptorHandlerTest extends MD5Test {

    @Override
    protected MessageDigestContainerDescriptorHandler newInstance() {
        return new MessageDigestContainerDescriptorHandler();
    }
}
