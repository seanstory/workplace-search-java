# Workplace Search - Java APIs
![](https://travis-ci.org/seanstory/workplace-search-java.svg)

A JVM client and SDK for Elastic Workplace Search

### Building
To build, run:

    mvn clean install

### As a Dependency
To use the JVM client for Workplace Search, add:

```
            <dependency>
                <groupId>com.seanjstory.workplace.search</groupId>
                <artifactId>workplace-search-client</artifactId>
                <version>7.9.0-SNAPSHOT</version>
            </dependency>
```

To use the JVM SDK for Custom API Sources for Workplace Search, add:

```
            <dependency>
                <groupId>com.seanjstory.workplace.search</groupId>
                <artifactId>workplace-search-sdk</artifactId>
                <version>7.9.0-SNAPSHOT</version>
            </dependency>
```

### Usage
#### Client
The client requires an `accessToken`, and most of the APIs also require a `contentSourceKey`. Both can be found in the Workplace
Search UI after creating a Custom API source. Using the client is as easy as:

```groovy
import com.seanjstory.workplace.search.client.Client;
//...
String accessToken = "3a423c597442eddb09baad64793ff342fc0aa6da357f5227888d44b3386cf722";
String contentSourceKey = "5e8f5266f74c321dae6e5548";

Client client = new Client(accessToken);

Map<String,String> document = Collections.singletonMap("id", "someId")
List<Map<String, Object>> documents = Collections.singletonList(document)

client.indexDocuments(contentSourceKey, documents)
```

#### SDK

For experiential learners, the best way to see the SDK in action might be to look at [a simple example](https://github.com/seanstory/ws-custom-source-example).

The Workplace Search Java SDK is intended to help develop Custom API sources. To leverage it, it's worth understanding
what is in the module.

```
.
└── com.seanjstory.workplace.search.sdk
    ├── api
    │   ├── DocumentBase.java     # Abstract Class
    │   ├── Source.java           # Interface
    │   ├── SourcesUtils.java     # Concrete Utility
    │   ├── YamlConfig.java       # Concrete Utility
    │   └── Yielder.java          # Abstract Utility
    └── run
        └── Sync.java             # Concrete Utility
```

* The `DocumentBase` class provides the base API for "documents". Every document should have an id and a body. Most documents
will have other metadata as well (URL, Title, Last Modified Date, etc).
* The `Source` interface is merely defined as a class that provides an `Iterator<? extends DocumentBase>`.
* The `SourcesUtils` class is a lightweight approach to dependency injection. It will look for a `sources` resource in your classloader's resources, expecting to find a line-separated list of `Source` implementation cannonical names. The `SourceUtils` can then be used to instantiate a source, given its name, and optionally a list of String arguments.
* The `YamlConfig` class is a lightweight adapter for a YAML config file that can be used store your Access Token, Content Source Key, and any arguments necessary to instantiate your Sources (in conjunction with `SourcesUtils`).
* The `Yielder` is the recommended mechanism through which your `Source` implementations might provide an `Iterator`. Many sources may contain so many documents (and such large documents), that it becomes difficult to handle them all in a single collection. This lazy evaluator is easy to leverage, and can help you to keep only a small number of documents in memory at a given time.
* Finally, the `Sync` class provides a `main` function that drive your source. It expects two arguments, a path to a YAML configuration file (see `YamlConfig`) and a source name (see `SourcesUtils`). From these, it will instantiate a `Source`, connect to Workplace Search with a Java `Client`, and iterate over your documents, indexing them in batches of 100.
