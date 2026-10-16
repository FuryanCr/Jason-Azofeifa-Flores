/**
 * Interfaz de autenticación: declara el método encargado de la
 * recuperación de contraseña.
 */
public interface IAutenticacion {

    /**
     * Registra una nueva contraseña para el usuario identificado por su correo.
     * La contraseña se guarda con SHA-256 + salt aleatorio, nunca en texto plano.
     *
     * @param correo          correo que identifica al usuario
     * @param nuevaContrasena nueva contraseña en texto plano (solo en memoria)
     * @return true si se actualizó; false si no existe un usuario con ese correo
     */
    boolean recuperarContrasena(String correo, String nuevaContrasena);
}
