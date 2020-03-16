package de.sfuhrm.maven.assemblyplugin;

public class SHA256Test extends AbstractMessageDigestTest {

    @Override
    protected String getAbcSum() {
        return "ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad";
    }

    @Override
    protected String getTestSum() {
        return "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855";
    }

    @Override
    protected String getChecksumFile() {
        return "SHA256SUMS";
    }

    @Override
    protected MessageDigestContainerDescriptorHandler newInstance() {
        return new SHA256();
    }
}
