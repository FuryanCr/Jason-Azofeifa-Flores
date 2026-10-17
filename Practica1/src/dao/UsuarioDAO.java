package dao;

import interfaces.IAutenticacion;

public class UsuarioDAO implements IAutenticacion {

    @Override
    public boolean recuperarContrasena(String usuario, String correo) {
        // En esta práctica solo se pide la estructura del método; la consulta
        // a la base de datos y el envío del correo no forman parte del alcance.
        return usuario != null && correo != null;
    }
}
