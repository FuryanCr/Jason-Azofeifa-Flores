package dao;

public interface UsuarioDAO extends IAutenticacion {

    @Override
    boolean recuperarContrasena(String correo, String nuevaContrasena);

    boolean verificarContrasena(String correo, String contrasena);
}
