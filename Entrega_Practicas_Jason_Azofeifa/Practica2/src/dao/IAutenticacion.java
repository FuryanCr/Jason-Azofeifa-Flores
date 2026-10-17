package dao;

public interface IAutenticacion {

    /**
     * Cambia la contraseña del usuario que tenga ese correo.
     * La contraseña se guarda con SHA-256 y salt, nunca en texto plano.
     *
     * @return false si no existe un usuario con ese correo
     */
    boolean recuperarContrasena(String correo, String nuevaContrasena);
}
