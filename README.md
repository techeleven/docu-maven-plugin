# Docu Maven Plugin

This plugin allows you to duplicate parts of your source code based API documentation. Write once, use often.  

At [tech11](https://tech11.com), the source code is the basis for our API documentation. We use

* [JavaDoc plugin](https://maven.apache.org/plugins/maven-javadoc-plugin/) for Java APIs

* [enunciate](https://github.com/stoicflame/enunciate/wiki) for REST-APIs



## What is the docu-maven-plugin for?

### Different clients with same API description

You provide REST Web Services to your customers. With *enunciate* you can directly generate the REST API 
documentation based on your JavaDoc comments in your JAX-RS resource class/interface.

That's fine! But in case your customers are Java fan boys, you would like to provide also a Java client for this REST Web Service.

Having this scenario, it's often the case that many descriptions of the Web Service resources and identically with the 
documentation of the the Java client classes. The *docu-maven-plugin* supports you to write your documentation only once and use it on different documentation artefacts.

### Share documentation across projects

In a microservice landscape each service has its own data model. Nevertheless, from a business point of view many 
objects or also only attributes from these different services are identically.

For example, the attribute `premiumNet` is used in different data model of different services (policy, tariff, 
accounting, etc.). With the *docu-maven-plugin* you can write the detailed description of this attribute once 
and use it in different services.



## How is it working?

There's no magic! ;-)

The plugin reads just a HTML file as a kind of *documentation repository*. The comments inside the source code, which
are the base for the documentation, contains just a *flag* with an ID of the corresponding section in the documentation 
repository. 

The documentation is generated as as usual by your build script.

* maven-javadoc-plugin:jar (HTML files inside a jar file)

* com.webcohesion.enunciate:enunciate-maven-plugin:docs (HTML files inside a directory)

* maven-source-plugin:jar (Java files inside a jar file)  
  IDEs do not use the *artefact-javadoc.jar* file from Nexus/Artifactory for providing API documentation. 
  If available, they will use the *artefact-sources.jar*. Therefore, also the *maven-source-plugin* is listed here.

The *docu-maven-plugin* replaces inside the generated output artefacts (directory or jar/zip archive) the marked 
*flag* with the content of the *documentation repository*.

That's all.



## How to use?

At tech11 we run the *docu-maven-plugin* only when we deploy a new version of our artefacts to Nexus.

This example use the documentation at Javadoc, sources and REST documentation.

### pom.xml

```xml
...
<!--for example inside a deploy-profile section 
    
   -->
<build>
  ...
  <!-- configure the source, javadoc and enunciate plugins -->
  ...
  <plugin>
    <groupId>com.tech11</groupId>
    <artifactId>docu-maven-plugin</artifactId>
    <version>0.9.4</version>
    <executions>
      <execution>
        <id>docu-client</id>
        <phase>package</phase>
        <goals>
          <goal>jar</goal>
        </goals>
        <configuration>
          <docuRepoPath>${project.basedir}/src/assembly/docu-repo/docu-repo.html</docuRepoPath>
          <jarFile>${project.build.directory}/${project.artifactId}-${project.version}-javadoc.jar</jarFile>
        </configuration>
      </execution>
      
      <execution>
        <!-- IDEs use the source code for showing the JavaDoc -->
        <id>source-client</id>
        <phase>package</phase>
        <goals>
          <goal>jar</goal>
        </goals>
        <configuration>
          <fileSuffix>java</fileSuffix>
          <docuRepoPath>${project.basedir}/src/assembly/docu-repo/docu-repo.html</docuRepoPath>
          <jarFile>${project.build.directory}/${project.artifactId}-${project.version}-sources.jar</jarFile>
        </configuration>
      </execution>
      
      <execution>
        <id>docu-server</id>
        <phase>${phase-release-rest-api}</phase>
        <goals>
          <goal>dir</goal>
        </goals>
        <configuration>
          <docuRepoPath>${project.basedir}/src/assembly/docu-repo/docu-repo.html</docuRepoPath>
          <sourceDir>${project.build.directory}/site/rest-docs</sourceDir>
        </configuration>
      </execution>
    </executions>
  </plugin>
  
  ...
</build>
```

### docu-repo.html

Just an extract of the documentation repository `${project.basedir}/src/assembly/docu-repo/docu-repo.html`:

```html
<body>
  <h1>Accounting Services</h1>
  <div id="ACCOUNTING-general">General information how the accounting records are organized.</div>

  <div id="ACCOUNTING-processNewContractVersion">
    Each new contract version, which is premium relevant, starts a (re)calculation of the accounting records.
  </div>
  <div id="ACCOUNTING-processNewContractVersion-param-version">
    the data structure with all relevant contract data for the accounting records.
  </div>
  <div id="ACCOUNTING-processNewContractVersion-return">
    data structure for all accounting records for the current main due date frame.
  </div>
  ...
</body>
```

### Java JAX-RS resource class

```java
/**
 * #docu: ACCOUNTING-general
 */
@Stateless
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path(PATH_ACCOUNTING_RECORDS)
public class AccountingResource {

  /**
   * #docu: ACCOUNTING-processNewContractVersion
   * 
   * @param contractVersion
   *            #docu: ACCOUNTING-processNewContractVersion-param-version
   * @return #docu: ACCOUNTING-processNewContractVersion-return
   */
  @POST
  @TypeHint(MainDueDateFrame.class)
  public Response processNewContractVersion(@TypeHint(ARContractVersion.class) String contractVersion) {
    ...
  }
}
```

### Java client class

```java
/**
 * #docu: ACCOUNTING-general
 */
public class AccountingClient {
  
  /**
   * #docu: ACCOUNTING-processNewContractVersion
   * 
   * @param version
   *            #docu: ACCOUNTING-processNewContractVersion-param-version
   * @return #docu: ACCOUNTING-processNewContractVersion-return
   */
  public MainDueDateFrame processNewContractVersion(ARContractVersion version) {
    ...
  }
  ...
}
```

As you can see, the class `AccountingResource` and `AccountingClient` share the same documentation.


## Configuration

### Documentation repository

This documentation repository can be configured via the parameter `docuRepoPath`.

### Source directories

If the sources are just in an directory the directory must be configured via the parameter `sourceDir`.

### Jar/Zip files

If the source are inside an jar/zip archive, the file must be configured via the parameter `jarFile`.

### Pattern

Per default the *marker* is `#docu: KEY`. The *marker* is also configurable as 
plugin parameter `docuPattern`.

### Files

The default files are files ending with the suffix `html`. This can be configured via the parameter `fileSuffix`.
Please see also the example for *source-client* from *pom.xml* above.



## What's next?

### Configuration

The configuration of the `docuRepoPath` will be extended and build more flexible. 
Repo shared between more files and son on...

### Maven Plugin Site

Building a own plugin site where the maven goals are described in detail.

### Publishing

This plugin is still in a beta phase. This means, at tech11 we will use it and test if it works for our purpose.
If there plugin is a bit more maturity, we will also publish it to central maven repository.



## Contribute

* If you find bugs, please let us know. We will try to fix shortly.

* You prefer to fix bugs yourself. You're welcome :-) Please do git pull request

* You have ideas how to improve the plugin? Please let us know!