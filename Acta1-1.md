# Acta 1 sesión 1 : Creación de los primeros Logs
### Información de la reunión:
**Fecha:**  12/04/2022

**Hora:**  17:00 h
**Identificador del grupo:** 3ti21_G2
**Tipo de reunión:** Online
**Plataforma:** Discord
**Asistentes:**
Sales García, Gabriel
Pompilio, Matteo
### 1. Resumen de la reunión:
Hemos creado un proyecto web dinámico en el cual se han creados 2 Servelts (Log0, Log1), donde Log1 está basado en evoluciónes de las funcionalidades de Log0: para cada Servlet vamos a mostrar como hemos resuelto el problema requerido.
Además, el desarrollo de una pagina *index.html*, desde la cual es posible inserir los datos del usuario (username y password) para cada Servlet diferente, así que este pueda recibir los 2 datos y eventualmente procesarlos.
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
Se han importados las librerías *java.io.File* y *java.io.FileWriter* para la creación y la escritura de los datos en el fichero *LOG1.txt*, entre de un mecanismo try-catch:
```java
try {
  File myObj = new File(path);
  if (myObj.createNewFile()) {
    System.out.println("File created: " + myObj.getName());
    FileWriter textFile = new FileWriter(myObj);
    String res = path + "\n" + fechaHora + "\n" + user + "\n" + password + "\n" + URI;
    textFile.write(res);
    textFile.close();
  } else {
    System.out.println("File already exists.");
  }
} catch (IOException e) {
  System.out.println("An error occurred.");
  e.printStackTrace();
}
```
Donde la variable *path* se obtene añadendo el nombre del file a la ruta del mismo: ```this.getServletContext().getRealPath("/")```
### 4. La pagina de index
Aqí se muestra el codigo, muy sencillo, de los forms del Servlet Log0, que permiten de invocar ambos los métodos GET y POST.
Este codigo será el mismo para los 2 siguientes Servlets Log1 y Log2:
```html
<h2><span>LOG 0 - Impresión en pantalla</span></h2>
<form action="Log0" method="GET">
    <h4>MÉTODO POR GET</h4>
    Usuario: 
    <input type="text" name="user" required="required"><br>
    Contraseña: 
    <input type="password" name="password" required="required"><br>
    <input type="submit" value="Enviar">
</form>
<form action="Log0" method="POST">
    <h4>MÉTODO POR POST</h4>
    Usuario: 
    <input type="text" name="user" required="required"><br>
    Contraseña: 
    <input type="password" name="password" required="required"><br>
    <input type="submit" value="Enviar">
</form>
```
### 5. Objetivos para la próxima reunión:
**Las tareas a preparar para la siguiente sesión son:**
- estudio del funcionamento del fichero *web.xml*
- indrodución a los tags que permiten de crear variables de contexto

*Valencia, 13/04/2022, Matteo Pompilio*
