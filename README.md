# Práctica 1 - Funcionalidad "Olvidó mi contraseña"

Java Swing + patrón DAO.

- `src/FrmOlvido.java`: JFrame centrado, no redimensionable, cierre con DISPOSE, validación de campos y correo.
- `src/UsuarioDAO.java`: interfaz con `recuperarContrasena(usuario, correo)`.
- `src/UsuarioDAOImpl.java`: implementación (solo estructura).
- `src/FrmLogin.java`, `src/Main.java` y `src/Estilo.java`: ventana principal y estilo visual compartido.

Ejecutar: `java -jar Practica1.jar` (o compilar `javac src/*.java` y `java -cp . Main`).
