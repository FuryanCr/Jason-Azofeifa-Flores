package clases;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/** Salt aleatorio y hash SHA-256(salt + contraseña). Ambos se manejan en Base64. */
public final class ClsSeguridad {

    private static final int BYTES_SALT = 16;
    private static final SecureRandom azar = new SecureRandom();

    private ClsSeguridad() {
    }

    public static String nuevoSalt() {
        byte[] salt = new byte[BYTES_SALT];
        azar.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    public static String hash(String clave, String salt) {
        try {
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            sha.update(Base64.getDecoder().decode(salt));
            return Base64.getEncoder().encodeToString(sha.digest(clave.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Este equipo no tiene SHA-256", e);
        }
    }

    public static boolean coincide(String clave, String salt, String hashGuardado) {
        byte[] calculado = hash(clave, salt).getBytes(StandardCharsets.UTF_8);
        return MessageDigest.isEqual(calculado, hashGuardado.getBytes(StandardCharsets.UTF_8));
    }
}
