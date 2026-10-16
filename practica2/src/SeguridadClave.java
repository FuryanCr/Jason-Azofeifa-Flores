import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/** Utilidades de seguridad: salt aleatorio y hash SHA-256. */
public final class SeguridadClave {

    private static final int TAM_SALT = 16; // 128 bits
    private static final SecureRandom RANDOM = new SecureRandom();

    private SeguridadClave() { }

    /** Genera un salt aleatorio criptográficamente seguro (Base64). */
    public static String generarSalt() {
        byte[] salt = new byte[TAM_SALT];
        RANDOM.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    /** SHA-256(salt + contraseña), devuelto en Base64. */
    public static String hash(String contrasena, String saltBase64) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(Base64.getDecoder().decode(saltBase64));
            byte[] h = md.digest(contrasena.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(h);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 no disponible", e);
        }
    }

    /** Comparación en tiempo constante. */
    public static boolean coincide(String contrasena, String saltBase64, String hashEsperado) {
        byte[] a = hash(contrasena, saltBase64).getBytes(StandardCharsets.UTF_8);
        byte[] b = hashEsperado.getBytes(StandardCharsets.UTF_8);
        return MessageDigest.isEqual(a, b);
    }
}
