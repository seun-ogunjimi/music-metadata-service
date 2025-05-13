# Music Metadata Service
Music Metadata Service is a service that provides apis to get music metadata for artists and tracks.
# Getting Started
### Installation
>
Ensure you have `java >=1.8` runtime installed in your environment. You also need to install  **[maven](https://maven.apache.org/download.cgi)** to run the commands for setup.
1.  Unpack the project to any directory on your computer

2.  Launch a command line terminal on your Mac or PC and change to the directory
```
cd music-metatdata-service
```
3.  RUN Development : Quick startup as exploded source.
```
mvn spring-boot:run
``` 
4.  RUN Production: Archived packaging as `.jar` extension.

```
mvn clean install

java -jar ./target/music-metatdata-service-1.0.0.jar
```        
5. Open a browser at the address `http://localhost:9090/`

###  Language and Framework
>
1. Java
2. Spring Boot
3. Open Api
4. Hibernate
5. JUnit  (Hamcrest - assertions) (assertj - fluent assertions)
