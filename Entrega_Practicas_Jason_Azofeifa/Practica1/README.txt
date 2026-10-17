PRÁCTICA 1 - Ventana "Olvidó mi contraseña"
=============================================

Cómo ejecutarla
  Windows:      doble clic en ejecutar.bat   (o:  java -jar Practica1.jar)
  Mac / Linux:  ./ejecutar.sh               (o:  java -jar Practica1.jar)
  Requiere Java 17 o superior.

Qué hay en cada carpeta
  src/      código fuente (paquetes app, dao, util, vista, vista.ui)
  pruebas/  PruebasLogica y PruebasVentana (ver más abajo)

Clases principales
  vista.FrmOlvido        JFrame centrado, no redimensionable, cierre DISPOSE_ON_CLOSE
  dao.UsuarioDAO         interfaz con recuperarContrasena(usuario, correo)
  dao.UsuarioDAOImpl     implementación (solo la estructura, sin consultas a BD)
  util.Validador         campos vacíos / solo espacios / formato de correo
  vista.FrmLogin         ventana principal desde la que se abre FrmOlvido

Compilar a mano
  javac -encoding UTF-8 -d out $(find src -name "*.java")       (Mac/Linux)
  En NetBeans/Eclipse: importar la carpeta src como código fuente existente.

Pruebas
  javac -encoding UTF-8 -cp out -d out pruebas/*.java
  java -cp out PruebasLogica
  java -cp out PruebasVentana evidencia        (abre las ventanas de verdad)
