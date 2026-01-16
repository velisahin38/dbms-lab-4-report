package app.store;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import app.model.Student;
import com.google.gson.Gson;

public class RedisStore {
    // Tekil Jedis yerine Pool kullanıyoruz, yoksa siege testinde patlar
    static JedisPool pool;
    static Gson gson = new Gson();

    public static void init() {
        pool = new JedisPool("localhost", 6379);
        
        // Veri yükleme (Seeding)
        try (Jedis jedis = pool.getResource()) {
            System.out.println("Redis'e veri yükleniyor...");
            for (int i = 0; i < 10000; i++) {
                // ID formatı: 2025000001
                String id = String.valueOf(2025000001 + i);
                Student s = new Student(id, "Ad Soyad " + i, "Bilgisayar");
                jedis.set(id, gson.toJson(s));
            }
        }
    }

    public static Student get(String id) {
        // Her istekte havuzdan temiz bir bağlantı alıyoruz
        try (Jedis jedis = pool.getResource()) {
            String json = jedis.get(id);
            return gson.fromJson(json, Student.class);
        }
    }
}