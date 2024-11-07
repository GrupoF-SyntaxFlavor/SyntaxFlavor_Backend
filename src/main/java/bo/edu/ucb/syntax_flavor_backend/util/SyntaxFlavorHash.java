package bo.edu.ucb.syntax_flavor_backend.util;


import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class SyntaxFlavorHash {
    
    public SyntaxFlavorHash() {
    }
    public String hashPassword(String password) {
        try {
            // Crea una instancia de MessageDigest para SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            
            // Convierte la contraseña en bytes y realiza el hash
            byte[] hashBytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));

            // Convierte los bytes del hash en un string hexadecimal
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            // Retorna el hash como cadena
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al generar el hash de la contraseña", e);
        }
    }

    // Método para comparar una contraseña con su hash
    public boolean verifyPassword(String password, String storedHash) {
        // Hashear la contraseña proporcionada
        String hashedPassword = hashPassword(password);

        // Comparar el hash proporcionado con el hash almacenado
        return hashedPassword.equals(storedHash);
    }
}