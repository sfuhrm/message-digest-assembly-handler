Message Digest Assembly Handler
===================

[![Build Status](https://travis-ci.org/sfuhrm/message-digest-assembly-handler.svg)](https://travis-ci.org/sfuhrm/message-digest-assembly-handler)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/de.sfuhrm/message-digest-assembly-handler/badge.svg)](https://maven-badges.herokuapp.com/maven-central/de.sfuhrm/message-digest-assembly-handler) 
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![ReleaseDate](https://img.shields.io/github/release-date/sfuhrm/message-digest-assembly-handler)](https://github.com/sfuhrm/message-digest-assembly-handler/releases)

This is a Plugin for the [Maven Assembly Plugin](http://maven.apache.org/plugins/maven-assembly-plugin/) that calculates
Message Digests for the files archived. A possible outcome is a 'MD5SUM' file or 'SHA256SUM' file.

### Requirements

The message-digest-assembly-handler requires at least the following environment:
* JDK 8 and later
* Maven-Assembly-Plugin 3.2.0 and later

### Usage

You need to add the handler to your `pom.xml` and your assembly descriptor file.
Examples are shown in the next subsections.

#### In your POM

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

#### In your assembly descriptor

In your XML assembly descriptor, add one or more `containerDescriptorHandler` elements:

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
        <checksumFile>MD5SUM</checksumFile>
      </configuration>
    </containerDescriptorHandler>
  </containerDescriptorHandlers>
```xml
