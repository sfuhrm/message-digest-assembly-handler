package de.sfuhrm.maven.assemblyplugin;

public class MD5Test extends AbstractMessageDigestTest {
    @Override
    protected String getAbcSum() {
        return "900150983cd24fb0d6963f7d28e17f72";
    }

    @Override
    protected String getTestSum() {
        return "d41d8cd98f00b204e9800998ecf8427e";
    }

    @Override
    protected String getChecksumFile() {
        return "MD5SUMS";
    }

    @Override
    protected MessageDigestContainerDescriptorHandler newInstance() {
        return new MD5();
    }
}
