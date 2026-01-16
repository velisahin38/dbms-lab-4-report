package app;

import static spark.Spark.*;
import com.google.gson.Gson;
import app.store.*;
import app.model.Student;

public class Main {
    public static void main(String[] args) {
        // Portu 8081 yaptık ki arkada açık kalan varsa çakışmasın
        port(8081); 
        Gson gson = new Gson();

        try {
            RedisStore.init();
            HazelcastStore.init();
            MongoStore.init();
        } catch (Exception e) {
            System.err.println("Veritabanı başlatma hatası: " + e.getMessage());
        }

        // --- REDIS ENDPOINT ---
        // URL'deki 'student_no=2025...' kısmının tamamını 'param' olarak alıyoruz
        get("/nosql-lab-rd/:param", (req, res) -> {
            res.type("application/json");
            
            // Gelen veri: "student_no=2025000001"
            String param = req.params(":param");
            // Eşittir işaretinden sonrasını (ID'yi) al
            String id = param.contains("=") ? param.split("=")[1] : param;

            Student s = RedisStore.get(id);
            return s != null ? gson.toJson(s) : "{\"hata\":\"Redis'te veri yok: " + id + "\"}";
        });

        // --- HAZELCAST ENDPOINT ---
        get("/nosql-lab-hz/:param", (req, res) -> {
            res.type("application/json");
            String param = req.params(":param");
            String id = param.contains("=") ? param.split("=")[1] : param;

            Student s = HazelcastStore.get(id);
            return s != null ? gson.toJson(s) : "{\"hata\":\"Hazelcast'te veri yok: " + id + "\"}";
        });

        // --- MONGODB ENDPOINT ---
        get("/nosql-lab-mon/:param", (req, res) -> {
            res.type("application/json");
            String param = req.params(":param");
            String id = param.contains("=") ? param.split("=")[1] : param;

            Student s = MongoStore.get(id);
            return s != null ? gson.toJson(s) : "{\"hata\":\"Mongo'da veri yok: " + id + "\"}";
        });
        
        System.out.println("SİSTEM HAZIR! Şu linke tıkla:");
        System.out.println("http://localhost:8081/nosql-lab-rd/student_no=2025000001");
    }
}