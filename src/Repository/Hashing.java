package Repository;

import org.mindrot.jbcrypt.BCrypt;

public interface Hashing {
    // hashea -> Cuando guardo 
    public static String hash(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
    // verifica -> cuando lo traigo de la bdd
    public static boolean verificar(String password, String hash) {
        return BCrypt.checkpw(password, hash);
    }
}