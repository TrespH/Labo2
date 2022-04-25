# Acta 1 sesión 1 : Creación de los primeros Logs
### Información de la reunión:
**Fecha:**  21/04/2022
**Hora:**  17:00 h
**Identificador del grupo:** 3ti21_G2
**Tipo de reunión:** Online
**Plataforma:** Discord
**Asistentes:**
Sales García, Gabrielh
Malerba, Alessio
Pompilio, Matteo
### 1. Resumen de la reunión:
Hemos creado un proyecto web dinámico en el cual se han creados 3 Servelts (Log0, Log1, Log2), donde Log1 y Log2 son basadas en evoluciónes de las funcionalidades de Log0: para cada Servlet vamos a mostrar como hemos resuelto el problema requerido.
Además, el desarrollo de una pagina *index.html*, desde la cual es posible inserir los datos del usuario (username y password) para cada Servlet diferente, así que este pueda recibir los 2 datos y eventualmente procesarlos.
El último file donde se han hecho modificaciones es el *web.xml*, con respecto al Servlet Log2.
En el desarrollo en generál no se han encontrados grandes problemas: el proyecto se ha crado y desarrollado enteramente en la VM, entonces el único limite para un trabajo fluido fueron las bajas prestaciones de la conexión con la máquina DEW.
### 2. Desarrollo de Log0
Se ha intervenido principalmente en la función *doGet()*, en la qual se han recogido los 2 parametros de input del usuario y otras variables propias de la solicitúd HTTP:
```java
String fechaHora =  LocalDateTime.now().toString();
String user = request.getParameter("user");
String password = request.getParameter("password");
String URI = request.getRequestURL() + request.getContextPath() + 
             request.getServletPath() + request.getPathInfo();
```
Y imprimidas en pantalla en formato HTML:
```java
response.setContentType("text/html");
PrintWriter out = response.getWriter();
out.println("<!DOCTYPE html>\n<html lang='es-es'><head><title>Log0</title></head><body><table>");
out.println("<tr><td colspan=2><b>Info</b></td><tr>");
out.println("<tr><td>Usuario</td><td>"+user+"</td><tr>");
out.println("<tr><td>Contraseña</td><td>"+password+"</td><tr>");
out.println("<tr><td>Fecha y Hora</td><td>"+fechaHora+"</td><tr>");
out.println("<tr><td>Request URI</td><td>"+URI+"</td><tr>");
out.println("<tr><td colspan=2><b>Protocolo HTTP</b></td><tr>");
out.println("<tr><td>Metodo</td><td>"+request.getMethod()+"</td><tr>");			
out.println("</table></body></html>");
```
### 3. El Servlet Log1

### 4. Actualizaciónes con el Servlet Log2

### 5. Objetivos para la próxima reunión:
**Las tareas a preparar para la siguiente sesión son:**
- dd 
- dd
- dd

*Valencia, 25/04/2022, Matteo Pompilio*
