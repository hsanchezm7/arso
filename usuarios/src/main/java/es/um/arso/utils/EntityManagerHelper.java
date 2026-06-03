package es.um.arso.utils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class EntityManagerHelper {

    private static EntityManagerFactory entityManagerFactory;
    private static final ThreadLocal<EntityManager> entityManagerHolder;

    static {
        java.util.Map<String, String> properties = new java.util.HashMap<>();
        String dbUrl = System.getenv("USUARIOS_DB_URL");
        if (dbUrl == null)
            dbUrl =
                    "jdbc:mysql://localhost:3306/usuarios_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
        properties.put("javax.persistence.jdbc.url", dbUrl);

        String dbUser = System.getenv("DB_USER");
        if (dbUser == null) dbUser = "root";
        properties.put("javax.persistence.jdbc.user", dbUser);

        String dbPassword = System.getenv("DB_PASSWORD");
        if (dbPassword == null) dbPassword = "practicas";
        properties.put("javax.persistence.jdbc.password", dbPassword);

        entityManagerFactory = Persistence.createEntityManagerFactory("usuarios", properties);
        entityManagerHolder = new ThreadLocal<EntityManager>();
    }

    public static EntityManager getEntityManager() {
        EntityManager entityManager = entityManagerHolder.get();

        if (entityManager == null || !entityManager.isOpen()) {
            entityManager = entityManagerFactory.createEntityManager();
            entityManagerHolder.set(entityManager);
        }

        return entityManager;
    }

    public static void closeEntityManager() {
        EntityManager entityManager = entityManagerHolder.get();

        if (entityManager != null) {
            entityManagerHolder.set(null);
            entityManager.close();
        }
    }

    public static void closeEntityManagerFactory() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
            entityManagerFactory = null;
        }
    }
}
