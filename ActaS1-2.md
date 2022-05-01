# Acta 1 sesión 2 : Log2 y primeras dudas.
### Información de la reunión:
**Fecha:**  21/04/2022
**Hora:**  17:30 h
**Identificador del grupo:** 3ti21_G2
**Tipo de reunión:** Online
**Plataforma:** Discord
**Asistentes:**
Sales García, Gabrielh
Malerba, Alessio
Pompilio, Matteo
### 1. Resumen de la reunión:
Hemos continuado con las partes restantes de la sesión 1. Habiendo completado ya Log0 y Log1, hemos implementado una versión para Log2. El trabajo prosiguió sin obstáculos, pero luego tuvimos un pequeño problema con las partes restantes, a saber, Log3 y los comandos curl de prueba. 

### 2. Desarollo de Log2:
Para esto, hemos configurado la ruta del archivo "PrimeroMP" colocándolo en la sección context-param añadida dentro del archivo "web.xml", utilizando las etiquetas param-name y param-value. Aquì está el código añadido:

    <context-param>
        <param-name>Path</param-name>
        <param-value>/home/user/apache-tomcat-9.0.62/webapps/primeroMP/</param-value>
    </context-param>

En cuanto al código del servlet, en Log2.java utilizamos un método especial para encontrar la ruta del archivo, llamado getInitParameter. Esto fue una adición significativa al código java previamente escrito para Log0 y Log1, como puedes ver en el siguiente *snippet*:

	response.setContentType("text/html");
	String output = "";
	String path = this.getServletContext().getInitParameter("Path") + "LOG2.txt";
	PrintWriter out = response.getWriter();
	String fechaHora =  LocalDateTime.now().toString();
	String user = request.getParameter("user");
	String password = request.getParameter("password");
	String URI = request.getRequestURL() + request.getContextPath() + request.getServletPath() + request.getPathInfo();

### 3. Dudas sobre Log3:
Log3 fue el que nos detuvo, ya que no pudimos entender bien qué significaba convertir un servlet en un filtro. Por eso hemos decidido de enviar un correo al profe explicando directamente nuestras dudas, ya que es posible que el trabajo sólo deba realizarse en el futuro con conocimientos que aún no poseemos

### 4. EL comando *curl*:
Nuestro desconocimiento del comando curl, que también se utilizaba en Log2, nos hizo suspender la ruina más adelante. Hemos decidido hablar entre nosotros de todos modos en una futura reunión muy cercana para estudiar mejor y por nuestra cuenta cómo funcionaban las opciones individuales que teníamos que utilizar de la instrucción bash.

### 5. Objetivos para la próxima reunión:
**Las tareas a preparar para la siguiente sesión son:**
- Estudio del funcionamento del comando de bash *curl*
- Esperar que el profe nos conteste y discutir en el grupo whatsapp sobre que hacer sobre Log3

*Genova, 21/04/2022, Malerba Alessio*
