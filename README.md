# PORTAL SAE

Proyecto de pasantía por el estudiante `Santiago Cujaban Neita` para la empresa **SISCEM SAS** afiliada a la **Fundación Universitaria Juan de Castellanos**,
bajo la supervisión de `Viviana Alexandra Villanueva`.

## Instrucciones 📖

A continuación, se detallan los pasos que deben seguirse para editar el código desarrollado.

1. **Clona el repositorio de Github**.

2. **Instala Visual Studio Code**: este se usa en el desarrollo de Angular, por lo que se recomienda descargar las siguientes extensiones: Angular Language Service y Prettier, este ultimo para mantener el mismo formato.

3. **Instala** [Java JDK 17.0.9](https://download.oracle.com/java/17/archive/jdk-17.0.9_windows-x64_bin.exe)

4. **Instala** [SpringTools 4 For Eclipse](https://spring.io/tools): al descomprimir, recomiendo enviar la carpeta a "archivos de programa x86" y crear un acceso directo al escritorio del SpringToolSuite4.exe.

5. **Descarga** [Lombok.jar](https://projectlombok.org/download) y continúa con la configuración siguiendo las indicaciones detalladas en el siguiente [tutorial](https://www.youtube.com/watch?v=UKQdv3cu2Ok).

6. Abre Spring Tools 4, haz clic en "Import Project", elige la opción "Import from File System or Archive" y selecciona la carpeta "backend". Luego, haz clic derecho en la carpeta importada, elige "Run As" -> "Maven Clean" y "Maven Install". Finalmente, ejecuta "PortalSaeApplication" como "Spring Boot App".

7. **Instala** [NodeJS 20.11.1](https://nodejs.org/dist/v20.11.1/)

8. **Instala Angular 17.2.0**: después de instalar Node.js, abre el símbolo del sistema (CMD) y escribe el siguiente comando.

```
> npm install -g @angular/cli@17.2.0
> ng version
```

## Información Adicional 📚

El backend se creó mediante [Spring Boot Initializr](https://start.spring.io/), utilizando la siguiente configuración.

- Project
  - Maven
- Language
  - Java
- Spring Boot
  - 3.2.0
- Metadata
  - Group: com.siscem
  - Artifact: portal_sae
  - Name: portal_sae
  - Description: ""
  - Package Name: com.siscem.portal_sae
  - Packaging: jar
  - Java: 17
- Dependencies
  - MySQL Driver
  - Spring Web
  - Mapper
  - Lombok
  - Spring Boot DevTools
  - Spring Data JPA

Para iniciar el frontend, asegúrate de haber completado los pasos previos y luego introduce los siguientes comandos:

```
> cd frontend
> npm i
> npm start / ng serve -o
```
