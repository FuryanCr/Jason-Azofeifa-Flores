/**
 * Interfaz DAO para las operaciones relacionadas con usuarios.
 */
public interface UsuarioDAO {

    // ... aquí van los métodos que ya tenga su interfaz (login, registrar, etc.)

    /**
     * Declaración del método para recuperar la contraseña.
     *
     * @param usuario nombre de usuario ingresado en el formulario
     * @param correo  correo electrónico ingresado en el formulario
     * @return true si la solicitud de recuperación fue procesada
     */
    boolean recuperarContrasena(String usuario, String correo);
}
