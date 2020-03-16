package de.sfuhrm.maven.assemblyplugin;

public class SHA1Test extends AbstractMessageDigestTest {

    @Override
    protected String getAbcSum() {
        return "a9993e364706816aba3e25717850c26c9cd0d89d";
    }

    @Override
    protected String getTestSum() {
        return "da39a3ee5e6b4b0d3255bfef95601890afd80709";
    }

    @Override
    protected String getChecksumFile() {
        return "SHA1SUMS";
    }

    @Override
    protected MessageDigestContainerDescriptorHandler newInstance() {
        return new SHA1();
    }
}
