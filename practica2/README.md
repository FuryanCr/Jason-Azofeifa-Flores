# Práctica 2 - Regla de negocio: recuperación de contraseña

- `IAutenticacion`: declara `boolean recuperarContrasena(String correo, String nuevaContrasena)`.
- `UsuarioDAO`: interfaz que extiende `IAutenticacion` (+ `verificarContrasena`).
- `UsuarioDAOImpl`: implementa la lógica; guarda `correo;salt;hash` en `usuarios.txt`.
- `SeguridadClave`: salt aleatorio (SecureRandom, 16 bytes) y hash SHA-256(salt + contraseña).
- `FrmOlvido`: formulario (correo, nueva contraseña, confirmación) con validaciones.
- `PruebaSeguridad`: prueba de consola que demuestra el cifrado.

Cuenta de prueba: `demo@correo.com` (clave inicial `Demo1234`).
Ejecutar: `java -jar Practica2.jar`.  Prueba: `java -cp Practica2.jar PruebaSeguridad`.
