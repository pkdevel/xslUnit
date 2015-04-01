# xslUnit

## building an executable jar

`mvn clean jfx:jar`

- 'java -jar target/jfx/app/xslUnit-0.2.1-SNAPSHOT-jfx.jar'

## run the app

`mvn jfx:run`

## build an installer and executable
### might not work under windows, without external toolchains

`mvn clean jfx:native`

- 'target/jfx/native/bundles'
