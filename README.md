# Deney Sonu Teslimatı

Sistem Programlama ve Veri Yapıları bakış açısıyla veri tabanlarındaki performansı öne çıkaran hususlar nelerdir?

Aşağıda kutucuk (checkbox) ile gösterilen maddelerden en az birini seçtiğiniz açık kaynak kodlu bir VT kaynak kodları üzerinde göstererek açıklayınız. Açıklama bölümüne kısaca metninizi yazıp, kod üzerinde gösterim videonuzun linkini en altta belirtilen kutucuğa yerleştiriniz.

- [X]  Seçtiğiniz konu/konuları bu şekilde işaretleyiniz. **!**
    
---

# 1. Sistem Perspektifi (Operating System, Disk, Input/Output)

### Disk Erişimi

- [ ]  **Blok bazlı disk erişimi** → block_id + offset
- [ ]  Rastgele erişim

### VT için Page (Sayfa) Anlamı

- [ ]  VT hangisini kullanır? **Satır/ Sayfa** okuması

---

### Buffer Pool

- [ ]  Veritabanları, Sık kullanılan sayfaları bellekte (RAM) kopyalar mı (caching) ?

- [x]  LRU / CLOCK gibi algoritmaları
- [ ]  Diske yapılan I/O nasıl minimize ederler?

# 2. Veri Yapıları Perspektifi

- [ ]  B+ Tree Veri Yapıları VT' lerde nasıl kullanılır?
- [ ]  VT' lerde hangi veri yapıları hangi amaçlarla kullanılır?
- [ ]  Clustered vs Non-Clustered Index Kavramı
- [ ]  InnoDB satırı diskte nasıl durur?
- [ ]  LSM-tree (LevelDB, RocksDB) farkı
- [ ]  PostgreSQL heap + index ayrımı

DB diske yazarken:

- [ ]  WAL (Write Ahead Log) İlkesi
- [ ]  Log disk (fsync vs write) sistem çağrıları farkı

---

# Özet Tablo

| Kavram      | Bellek          | Disk / DB      |
| ----------- | --------------- | -------------- |
| Adresleme   | Pointer         | Page + Offset  |
| Hız         | O(1)            | Page IO        |
| PK          | Yok             | Index anahtarı |
| Veri yapısı | Array / Pointer | B+Tree         |
| Cache       | CPU cache       | Buffer Pool    |

---

# Video [Linki](https://youtu.be/dgHlb0y-NEA) 
Ekran kaydı. 2-3 dk. açık kaynak V.T. kodu üzerinde konunun gösterimi. Video kendini tanıtma ile başlamalıdır (Numara, İsim, Soyisim, Teknik İlgi Alanları). 

---

# Açıklama (Ort. 600 kelime)

Giriş: Veritabanı Performansının Temelleri Veritabanı Yönetim Sistemleri (VTYS) dersi kapsamında incelendiğinde, veritabanı performansını belirleyen en kritik faktör, bellek (RAM) ve disk (HDD/SSD) arasındaki erişim hızı farkıdır. Bir veritabanı mühendisi gözüyle bakıldığında, mekanik disk erişimi (I/O) en maliyetli işlemdir. Bu darboğazı aşmak için modern veritabanları, Buffer Pool adı verilen bir önbellekleme mekanizması kullanır. PostgreSQL üzerinde yapılan bu çalışmada, verilerin diskten "blok tabanlı" (Block-based access) okunarak bellekte Page (Sayfa) yapıları içinde nasıl tutulduğu ve bu sınırlı bellek alanının nasıl yönetildiği analiz edilmiştir. Ayrıca, rastgele erişim maliyetinden kaçınmak için WAL (Write Ahead Log) mekanizması ile sıralı yazma (sequential write) stratejilerinin önemi vurgulanmıştır.

Veri Yapıları Perspektifi Veritabanları, veriye erişimi hızlandırmak için standart veri yapılarının özelleştirilmiş hallerini kullanır. Örneğin, disk bloklarına uyumlu olması ve ağaç derinliğini minimize etmesi (daha az disk okuması) nedeniyle indekslemede B+ Tree yapısı tercih edilirken, bellekteki verilere O(1) sürede erişmek için Hash Tabloları kullanılır. Ancak Buffer Pool dolduğunda, hangi sayfanın bellekten atılacağına (eviction) karar vermek için kullanılan kuyruk yapıları ve algoritmalar sistemin genel başarısını belirler.

Kod Analizi: PostgreSQL freelist.c ve Algoritmalar Bu deneyde, PostgreSQL'in açık kaynak kodları arasındaki freelist.c dosyası incelenmiştir. Kod içerisinde #define LRU makrosu ile yapılan simülasyonda, LRU (En Az Son Kullanılan) algoritmasının çalışma mantığı gözlemlenmiştir. LRU, teorik olarak en uzun süredir kullanılmayan sayfayı "kurban" seçerek en doğru tahliyeyi yapsa da, pratik uygulamada büyük bir dezavantajı vardır: StrategyLRUEnqueue gibi fonksiyonlar, her veri erişiminde bellekteki çift yönlü bağlı listeyi (doubly linked list) güncellemek zorundadır. Bu durum, VTYS perspektifiyle bakıldığında, çok kullanıcılı sistemlerde yüksek "kilit çekişmesine" (lock contention) ve işlemci darboğazına neden olur.

PostgreSQL'in Çözümü: Clock Sweep Algoritması PostgreSQL, LRU'nun bu maliyetinden kaçınmak için Clock Sweep (Saat) algoritmasını kullanır. StrategyGetBuffer fonksiyonu içerisinde uygulanan bu yöntem, bellek sayfalarını dairesel bir döngü (ring buffer) içinde tarar. Maliyetli liste güncellemeleri yerine, sadece sayfa başlıklarındaki usage_count (kullanım sayacı) kontrol edilir. Algoritma, kullanım sayacı 0'dan büyük olan sayfalara "ikinci bir şans" vererek (usage_count--) onları bellekte tutar, sayacı 0 olanları ise tahliye eder. Ayrıca, o an kullanımda olan (pinned) sayfalar (refcount > 0) asla tahliye edilmez.

Sonuç: Yapılan inceleme sonucunda; Clock Sweep algoritmasının, veri yapısını sürekli kilitleme ihtiyacını ortadan kaldırarak LRU'ya kıyasla çok daha düşük sistem kaynağı tükettiği ve disk I/O işlemlerini minimize ederek veritabanının genel işlem hacmini (throughput) maksimize ettiği görülmüştür. Bu yaklaşım, sistem kaynaklarını verimli kullanarak yüksek performanslı bir VTYS mimarisi oluşturmanın temel taşlarından biridir.

## VT Üzerinde Gösterilen Kaynak Kodları

LRU ve Clock Sweep [Linki](https://github.com/chen3593/PostgreSQL/blob/master/freelist.c) \

