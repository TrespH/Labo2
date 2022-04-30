#Inicio sesion:
curl -s --data '{"dni":"23456733H","password":"123456"}' \ -X POST -H 
"content-type: application/json" http://dew-mpompil-2122.dsicv.upv.es:9090/CentroEducativo/login \ -H "accept: application/json" -c cucu -b cucu
#Respuesta: ij677rp0kh4371tgc5sopuctpo

#Consulta de alumnos y asignaturas:
curl -s -X GET 'http://dew-mpompil-2122.dsicv.upv.es:9090/CentroEducativo/alumnosyasignaturas?key=ij677rp0kh4371tgc5sopuctpo' \ -H "accept: application/json" -c cucu -b cucu
#Respuesta: [{"apellidos":"Garcia Sanchez","password":"123456","nombre":"Pepe","asignaturas":["DCU","IAP","DEW"],"dni":"12345678W"},{"apellidos":"Fernandez Gómez","password":"123456","nombre":"Maria","asignaturas":["DEW","DCU"],"dni":"23456387R"},{"apellidos":"Hernandez Llopis","password":"123456","nombre":"Miguel","asignaturas":["DCU","IAP"],"dni":"34567891F"},{"apellidos":"Benitez Torres","password":"123456","nombre":"Laura","asignaturas":["IAP","DEW"],"dni":"93847525G"},{"apellidos":"Alonso Pérez","password":"123456","nombre":"Minerva","asignaturas":[],"dni":"37264096W"}]

#Inicio sesion como administrador (necesario para añadir un nuevo usuario):
KEY= $(curl -s --data '{"dni":"111111111","password":"654321"}' \ 
-X POST -H "content-type: application/json" http://dew-mpompil-2122.dsicv.upv.es:9090/CentroEducativo/login \ -H "accept: application/json" -c cucu -b cucu)

#Añadir nuevo usuario:
curl -s --data '{"apellidos": "Rodrigo Sanchez", "dni": "33445566X", "nombre": "Bienvenido","password": "inkreible"}' \ -X POST -H "content-type: application/json" http://dew-mpompil-2122.dsicv.upv.es:9090/CentroEducativo/alumnos?key=%27$KEY \  -c cucu -b cucu

#Consulta de alumnos y asignaturas para comprobar si se ha añadido el ususario.
curl -s -X GET 'http://dew-mpompil-2122.dsicv.upv.es:9090/CentroEducativo/alumnosyasignaturas?key=%27$KEY' \ -H "accept: application/json" -c cucu -b cucu 
#Respuesta: [{"apellidos":"Garcia Sanchez","password":"123456","nombre":"Pepe","asignaturas":["IAP","DCU","DEW"],"dni":"12345678W"},{"apellidos":"Fernandez Gómez","password":"123456","nombre":"Maria","asignaturas":["DCU","DEW"],"dni":"23456387R"},{"apellidos":"Hernandez Llopis","password":"123456","nombre":"Miguel","asignaturas":["IAP","DCU"],"dni":"34567891F"},{"apellidos":"Benitez Torres","password":"123456","nombre":"Laura","asignaturas":["DEW","IAP"],"dni":"93847525G"},{"apellidos":"Alonso Pérez","password":"123456","nombre":"Minerva","asignaturas":[],"dni":"37264096W"},{"apellidos":"Rodrigo Sanchez","password":"inkreible","nombre":"Bienvenido","asignaturas":[],"dni":"33445566X"}]