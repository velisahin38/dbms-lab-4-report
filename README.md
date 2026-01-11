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

# Video [Linki](https://www.youtube.com/watch?v=Nw1OvCtKPII&t=2635s) 
Ekran kaydı. 2-3 dk. açık kaynak V.T. kodu üzerinde konunun gösterimi. Video kendini tanıtma ile başlamalıdır (Numara, İsim, Soyisim, Teknik İlgi Alanları). 

---

# Açıklama (Ort. 600 kelime)

Veritabanı sistemlerinin performans yönetimindeki temel amaç, işlemci ve bellek hızları ile mekanik disk erişim hızları arasındaki darboğazı (latency gap) aşmaktır; bu bağlamda sistem programlama perspektifinden bakıldığında, veritabanları Buffer Pool mekanizmalarıyla sık kullanılan verileri RAM'de önbellekleyerek maliyetli disk I/O işlemlerini minimize eder ve işletim sisteminin blok tabanlı okuma mantığına uygun olarak verileri Page (Sayfa) yapıları içinde tutarak tek seferde ilişkili verilere erişim sağlar (Spatial Locality). Ayrıca, rastgele disk erişiminin yavaşlığından kaçınmak için WAL (Write Ahead Log) gibi mekanizmalarla diske sıralı (sequential) yazma stratejileri uygulanır. Veri yapıları perspektifinden ise, standart arama ağaçlarının aksine disk bloklarına uygun, geniş dallanma yapısına sahip (high fan-out) B+ Tree yapıları kullanılarak ağaç yüksekliği düşük tutulur ve milyonlarca kayda çok az sayıda disk okumasıyla erişilmesi hedeflenir; buna ek olarak, sınırlı bellek alanının verimli kullanımı için LRU veya Clock Sweep gibi algoritmalarla en az kullanılan sayfaların tahliyesi (eviction) yönetilirken, bellekteki verilere hızlı erişim için Hash tabloları kullanılır.

Sistem Programlama ve Veri Yapıları perspektifiyle incelendiğinde, veritabanı yönetim sistemlerinin en kritik performans darboğazı, milisaniyeler süren mekanik disk erişimleri ile nanosaniyeler süren bellek erişimleri arasındaki hız farkıdır. Bu maliyetli disk I/O işlemlerini minimize etmek amacıyla PostgreSQL, shared_buffers alanında bir Buffer Pool mekanizması işletir. Bu çalışmada incelenen freelist.c kaynak kodunda, bellek dolduğunda devreye giren sayfa tahliye (page eviction) stratejileri analiz edilmiştir. Kod içerisindeki #define LRU makrosu ile aktif edilen klasik LRU (En Az Son Kullanılan) algoritması, teorik olarak en uygun kurban sayfayı seçse de, StrategyLRUEnqueue ve StrategyLRUDequeue fonksiyonlarının her veri erişiminde çift yönlü bağlı liste yapısını güncelleme zorunluluğu bulunmaktadır. Bu durum, yüksek eşzamanlılık gerektiren sistemlerde yoğun "kilit çekişmesine" ve işlemci darboğazına neden olmaktadır. Buna karşılık, PostgreSQL'in endüstri standardı olarak kullandığı Clock Sweep (Saat) algoritması, bellek sayfalarını dairesel bir veri yapısı üzerinde tarayarak çalışır. StrategyGetBuffer fonksiyonu içerisindeki bu döngü, maliyetli liste güncellemeleri yerine sayfa başlıklarındaki usage_count (kullanım sayacı) değerini kontrol eder; eğer sayaç 0'dan büyükse sayfaya "ikinci bir şans" vererek (usage_count--) bellekte tutar, 0 ise sayfayı tahliye eder. Bu yaklaşım, işletim sistemi seviyesindeki sayfa değişim mantığına benzer şekilde, minimum işlemci maliyetiyle maksimum I/O tasarrufu sağlayarak veritabanının genel işlem hacmini (throughput) optimize eder.

## VT Üzerinde Gösterilen Kaynak Kodları

Açıklama [Linki](https://...) \
Açıklama [Linki](https://...) \
Açıklama [Linki](https://...) \
... \
...
