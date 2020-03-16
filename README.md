Message Digest Assembly Handler
===================

[![Build Status](https://travis-ci.org/sfuhrm/message-digest-assembly-handler.svg)](https://travis-ci.org/sfuhrm/message-digest-assembly-handler)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/de.sfuhrm/message-digest-assembly-handler/badge.svg)](https://maven-badges.herokuapp.com/maven-central/de.sfuhrm/message-digest-assembly-handler) 
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/72c6b1b690e846389ab1faf93ff0091e)](https://www.codacy.com/manual/sfuhrm/message-digest-assembly-handler?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=sfuhrm/message-digest-assembly-handler&amp;utm_campaign=Badge_Grade)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![ReleaseDate](https://img.shields.io/github/release-date/sfuhrm/message-digest-assembly-handler)](https://github.com/sfuhrm/message-digest-assembly-handler/releases)

This is a plugin for the [Maven Assembly Plugin](http://maven.apache.org/plugins/maven-assembly-plugin/) that calculates
Message Digests for the files in the archive.
A typical possible outcome is a `MD5SUMS` file or `SHA256SUMS` file.

Requirements
--------------
The message-digest-assembly-handler requires at least the following environment:
*   JDK 8 and later
*   Maven-Assembly-Plugin 3.2.0 and later

Setup
--------------
You need to add the handler to your `pom.xml` and your assembly descriptor file.
Examples are shown in the next subsections.

### In your POM

In your `pom.xml`, add the `message-digest-assembly-handler` as a dependency to your `maven-assembly-plugin` plugin as described below.

```xml
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-assembly-plugin</artifactId>
  <version>3.2.0</version>
  <dependencies>
     <dependency>
       <groupId>de.sfuhrm</groupId>
       <artifactId>message-digest-assembly-handler</artifactId>
       <version>0.9.1</version>
     </dependency>
  </dependencies>
</plugin>
```

### In your assembly descriptor

In your XML assembly descriptor, add one `containerDescriptorHandler` element:

```xml
<?xml version="1.0"?>
<assembly>
  <id>my-id</id>
  <formats>
    <format>jar</format>
  </formats>
  <containerDescriptorHandlers>
    <containerDescriptorHandler>
      <handlerName>message-digest</handlerName>
      <configuration>
        <messageDigest>MD5</messageDigest>
        <checksumFile>MD5SUMS</checksumFile>
      </configuration>
    </containerDescriptorHandler>
  </containerDescriptorHandlers>
```

Instead of the `handlerName` `"message-digest"` you can also directly
refer to the shortcuts in the following table.

| Message Digest  | `handlerName` Parameter  | Default checksumFile | Note |
|---|---|---|---|
| MD5  | `message-digest-md5`  | `MD5SUMS` |   |
| SHA |  `message-digest-sha-1`| `SHA1SUMS`|  "SHA" in JDK refers to the SHA-1 algorithm  |
| SHA-256  | `message-digest-sha-256` | `SHA256SUMS` | SHA-2 |
| SHA-512  | `message-digest-sha-512` | `SHA512SUMS` | SHA-2 |

Configuration
--------------
The handler declaration in the assembly descriptor
understands the following configuration parameters:

*   `messageDigest`: The name of a MessageDigest algorithm from the JDK.
    Defaults to `MD5`.
    The following lists the MessageDigest algorithms supported by AdoptOpenJDK13:
    `MD2, MD5, SHA, SHA-224, SHA-256, SHA-384, SHA-512, SHA-512/224, SHA-512/256, SHA3-224, SHA3-256, SHA3-384, SHA3-512`.

*   `checksumFile`: The name of the checksum file generated. Defaults to
    `MD5SUMS`. Typical names are
    `MD5SUMS`, `SHA1SUMS`, and so on.

*   `exclusionRegex`: A regular expression that defines what files shall not
    be handled by this handler. This option is important if you want to create
    multiple checksum files with multiple `containerDescriptorHandler` sections.
    You can then exclude the handlers from each other, for example by
    giving the regex `(MD5SUMS|SHA1SUMS)`.
