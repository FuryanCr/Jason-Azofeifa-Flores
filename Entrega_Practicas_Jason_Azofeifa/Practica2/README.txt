PRÁCTICA 2 - Regla de negocio: recuperación de contraseña con SHA-256 + salt
============================================================================

Cómo ejecutarla
  Windows:      doble clic en ejecutar.bat   (o:  java -jar Practica2.jar)
  Mac / Linux:  ./ejecutar.sh               (o:  java -jar Practica2.jar)
  Requiere Java 17 o superior.

Cuenta de prueba:  demo@correo.com   /   Demo1234
Los usuarios se guardan en usuarios.txt (se crea junto al .jar la primera vez).
Para volver al estado inicial basta con borrar ese archivo.

Qué hay en cada carpeta
  src/      código fuente (paquetes app, dao, util, vista, vista.ui)
  pruebas/  PruebasLogica y PruebasVentana

Clases principales
  dao.IAutenticacion     declara recuperarContrasena(correo, nuevaContrasena)
  dao.UsuarioDAO         extiende IAutenticacion y agrega verificarContrasena
  dao.UsuarioDAOImpl     implementa la lógica; guarda  correo;salt;hash
  util.SeguridadClave    salt aleatorio (SecureRandom, 16 bytes) y SHA-256(salt + contraseña)
  util.Validador         validación de correo y fuerza de la contraseña
  vista.FrmOlvido        formulario: correo, nueva contraseña, confirmación
  vista.FrmLogin         inicio de sesión conectado al DAO

Compilar a mano
  javac -encoding UTF-8 -d out $(find src -name "*.java")       (Mac/Linux)
  En NetBeans/Eclipse: importar la carpeta src como código fuente existente.

Pruebas
  javac -encoding UTF-8 -cp out -d out pruebas/*.java
  java -cp out PruebasLogica
  java -cp out PruebasVentana evidencia        (abre las ventanas de verdad)
