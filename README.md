# jsainsburyplc-serverside-test

[![License](http://img.shields.io/:license-apache-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

This project is a demonstration implementation of the J Sainsbury's PLC serverside-test example found at https://jsainsburyplc.github.io/serverside-test/.

It is a multi-module Maven project which processes a specific website for products and extracts information as JSON.


## Building

### Default build

In order to build the application run the following command from the project root folder:

```
mvn -e clean package
```

In the projects sub-folder `application/target` is the main application which can be executed. 
Along-side the application is a configuration file `config.properties` which contains the URL and various parameters
to adjust the information extraction process.

### Build without test

In order to purely build the applicatin and skip the testing run the following command:

```
mvn -e clean package -Dmaven.test.skip=true
```

## Testing

In order to execute the tests separately run the following command from the project root folder:

```
mvn test
```

## Execution

In order to execute the application via command-line go to the output folder `application/target` and run the 
following command:

```
java -jar serverside-test-jar-with-dependencies.jar
```

The JSON output will be written to the standard console output.

If an error occurred it will be written to the error console output.
