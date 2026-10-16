/**
 * Interfaz DAO de usuarios. Hereda recuperarContrasena de IAutenticacion.
 */
public interface UsuarioDAO extends IAutenticacion {

    @Override
    boolean recuperarContrasena(String correo, String nuevaContrasena);

    /** Comprueba una contraseña contra el hash y salt almacenados. */
    boolean verificarContrasena(String correo, String contrasena);
}
