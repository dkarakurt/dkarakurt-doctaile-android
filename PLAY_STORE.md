# doctaile — Google Play yayın rehberi

Bu depo, Play'in istediği her şeyi üretir: imzalı **AAB** (Play'e yüklenen dosya), mağaza metinleri ve görselleri. Play Console'daki adımları hesap sahibinin yapması gerekiyor; aşağıdaki sıra tek seferlik kurulumdur.

## 0. Hazır olanlar
| Ne | Nerede |
|---|---|
| Hedef API 36 (Android 16), min. Android 7.0 | `app/build.gradle` |
| İmzalı AAB + APK | Her `main` push'unda GitHub Actions → Releases (`doctaile.aab`, `doctaile.apk`) |
| Mağaza metinleri (TR + EN) | `play-store/tr-TR`, `play-store/en-US` |
| İkon 512×512, öne çıkan görsel 1024×500 | `play-store/gorseller/` |
| Telefon ekran görüntüleri (5 adet, 1080×1920) | `play-store/gorseller/telefon-*.png` |
| Tablet / katlanabilir görüntüleri (2 adet, 1920×1200) | `play-store/gorseller/tablet-*.png` |
| Gizlilik politikası | `doctaile-web` deposunda `privacy.html` → `https://ALAN-ADIN/privacy.html` |

## 1. İmza anahtarını GitHub'a ekle (bir kez)
Depo → **Settings → Secrets and variables → Actions → New repository secret**. Dört secret ekle; değerler sana ayrıca verilen `doctaile-imza-anahtari.txt` dosyasında:

| Secret adı | Değer |
|---|---|
| `DOCTAILE_KEYSTORE_BASE64` | dosyadaki uzun base64 metin |
| `DOCTAILE_STORE_PASSWORD` | şifre |
| `DOCTAILE_KEY_PASSWORD` | aynı şifre |
| `DOCTAILE_KEY_ALIAS` | `doctaile` |

Sonra **Actions → APK ve AAB derle → Run workflow**. Releases'ta `doctaile.aab` görünür. Secret'lar yokken derleme yalnızca debug APK üretir (Play kabul etmez).

> `doctaile-upload.jks` dosyasını ve şifresini güvenli bir yerde sakla (parola yöneticisi + yedek). Bu bir **yükleme anahtarı**dır; Play App Signing açık olduğu için kaybolursa Play Console'dan sıfırlatılabilir, ama bu birkaç gün sürer.

## 2. Play Console hesabı
1. <https://play.google.com/console> → geliştirici hesabı aç (**kişisel**), 25 USD tek seferlik ücret, kimlik doğrulaması.
2. Kişisel hesaplar için kural: **en az 12 test kullanıcısı, aralıksız 14 gün kapalı test** yapılmadan üretime (herkese açık yayın) başvurulamaz.

## 3. Uygulamayı oluştur
**Uygulama oluştur** → Ad: `doctaile` · Varsayılan dil: Türkçe · Uygulama · Ücretsiz → beyanları onayla.

## 4. Uygulama içeriği (Policy → App content)
- **Gizlilik politikası:** `https://ALAN-ADIN/privacy.html`
- **Uygulama erişimi:** Tüm işlevler kısıtlamasız (e-posta girişi şifresiz, herkes girebilir).
- **Reklamlar:** Hayır.
- **İçerik derecelendirmesi (IARC):** Kategori "Diğer tüm uygulama türleri". Şiddet, cinsellik, kumar: Hayır. **Alkol: Evet** (tarifler alkollü içeceklere atıfta bulunur; satış yok). Kullanıcı etkileşimi / içerik paylaşımı: Hayır. Sonuç büyük ihtimalle 16+/18+ olur.
- **Hedef kitle:** Yalnızca **18 yaş ve üzeri**. ("Çocuklara hitap edebilir mi?" → Hayır.)
- **Veri güvenliği (Data safety):**
  - Uygulama kullanıcı verisi topluyor veya paylaşıyor mu? → **Hayır** (hiçbir veri cihazdan çıkmaz; internet izni yok).
  - Bu durumda veri türleri bölümü atlanır. Hesap silme: profil uygulama içinden silinebilir (giriş ekranı → profil yanındaki ×).
- **Haber uygulaması / COVID / devlet uygulaması / finans:** Hayır.

## 5. Mağaza girişi (Grow → Store presence → Main store listing)
| Alan | Dosya |
|---|---|
| Uygulama adı (≤30) | `play-store/tr-TR/baslik.txt` |
| Kısa açıklama (≤80) | `play-store/tr-TR/kisa-aciklama.txt` |
| Tam açıklama (≤4000) | `play-store/tr-TR/tam-aciklama.txt` |
| Uygulama simgesi | `play-store/gorseller/ikon-512.png` |
| Öne çıkan görsel | `play-store/gorseller/one-cikan-gorsel-1024x500.png` |
| Telefon ekran görüntüleri | `telefon-1.png` … `telefon-5.png` |
| 7 inç ve 10 inç tablet | `tablet-1.png`, `tablet-2.png` (ikisine de) |

- **Kategori:** Yiyecek ve İçecek · **Etiketler:** Tarifler, Yemek ve içecek
- **İletişim e-postası:** Play herkese açık bir destek e-postası ister; kendi adresini gir.
- İngilizce giriş eklemek için **Çeviri ekle → English (United States)** → `play-store/en-US/` dosyaları.

## 6. Kapalı test (14 gün)
1. **Test → Kapalı test → Kanal oluştur** → `doctaile.aab`'yi yükle (Releases'tan).
2. **Play App Signing**'i kabul et (varsayılan).
3. Test kullanıcıları: bir e-posta listesi oluştur, en az 12, tercihen 15–20 kişi ekle (Gmail adresleri).
4. Sürümü incelemeye gönder. Onaylanınca test bağlantısını paylaş; herkes bağlantıdan katılıp uygulamayı yüklemeli ve **14 gün boyunca katılımda kalmalı**.
5. Bu sürede yeni bir AAB yüklemek sayacı sıfırlamaz; test kullanıcılarının ayrılması sıfırlayabilir.

## 7. Üretime geçiş
14 gün dolunca **Panel → Üretime erişim başvurusu**: test sürecini ve geri bildirimleri anlatan kısa soruları yanıtla. Onaydan sonra **Üretim → Yeni sürüm** → aynı (ya da yeni) AAB → incelemeye gönder.

## Her güncellemede
`main`'e push → Actions yeni `versionCode` ile AAB üretir (`10 + derleme numarası`) → Play Console'da ilgili kanala yükle. İmza anahtarı aynı kaldığı için güncellemeler eskisinin üstüne veriler korunarak kurulur.

> Not: GitHub'dan APK ile kurulmuş eski sürümler (build-1…3) farklı bir anahtarla imzalandığı için Play sürümünün üstüne kurulamaz; Play'den kurmadan önce APK sürümünü kaldır.
