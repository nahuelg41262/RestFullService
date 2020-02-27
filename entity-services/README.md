# Introduccion 
Servicio ABM para manejo de Entidades

# Compilación

La dependencia de Oracle JDBC que se encuentra en el archivo `pom.xml` referencia al repositorio Maven de Oracle.

Para configurar dicho repositorio hay que tener un cuenta registrada en el mismo y configurar dicho repositorio en el archivo settings.xml (y el archivo `settings-security.xml` para alojar de manera segura la password).

El procedimiento para lo descripto anteriormente se describe en los pasos 2 al 4 de este [post](https://blogs.oracle.com/dev2dev/get-oracle-jdbc-drivers-and-ucp-from-oracle-maven-repository-without-ides#pom).

# Ejecución y Configuración
Para ejecutar el servicio es necesario tener intalada la openJDK 11 [OpenJDK Download](https://jdk.java.net/archive/)
Una vez compilado el proyecto se creara un archivo .jar el cual es el que debe ser ejecutado con el siguiente comando 
  
```sh
$ java -jar nombreDelJar
```

Una vez ejecutado el jar el servicio va a estar corriendo en el puerto 8080 , por defecto . 
Toda la Documentacion para la utilizacion del mismo se encuentra en http://localhost:8080/swagger-ui.html#/  

El archivo de configuracion se encuentra en el directorio /EntityRestService/src/main/resources llamado **application.properties**
    
##### El puerto donde corre el servicio es configurable desde 
    
   - server.port=8080

##### Credenciales de la base de datos .

   - spring.datasource.username=user
   - spring.datasource.password=secret

# Construccion
Para compilar el proyecto y generar el jar es necesario contar previamente con la intalacion de MAVEN [Maven Download](https://maven.apache.org/surefire/download.cgi#).}
Una vez instalado Complementario a esto es necesario agregar al repositorio local de MAVEN el driver necesario para conectarce con base de datos Oracle 12.2.0.1 , el cual esta adjuntado al proyecto " **ojdbc8.jar** " . 

Para agregar la dependencia al repositorio ejecutar . 

```sh
$ mvn install:install-file -Dfile=path/de/su/ojdbc8.jar -DgroupId=com.oracle -DartifactId=ojdbc8 -Dversion=12.2.0.1 -Dpackaging=jar
```

Una vez cumplidos lo dos requisitos previos pararce en la misma altura que se encuentra el archivo pom.xml y  ejecutar el siguiente comando    
 
```sh
$ mvn clean install
```

Esto genera el archivo .jar en la carpeta target/ con el nombre de EntityRestService-0.0.1-SNAPSHOT.jar
, el cual puede ejecutar como fue explicado en un comienzo . 
