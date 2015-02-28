# xslUnit

## fixing classpath for javaFX:
do this as super user

`mvn com.zenjava:javafx-maven-plugin:2.0:fix-classpath`

## building an executable jar
`mvn clean jfx:jar`

- 'target/jfx/app'

## run the app
`mvn jfx:run`

## build an installer
`mvn clean jfx:native`

- 'target/jfx/native/bundles'
