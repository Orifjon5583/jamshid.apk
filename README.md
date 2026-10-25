# jamshid.apk — Haftalik Xarajat Android Ilovasi

O'zbek tilidagi shaxsiy budjet, kundalik xarajatlar, daromadlar va qarzlarni nazorat qilish uchun to'liq Android ilovasi.

![Platform](https://img.shields.io/badge/Platform-Android%207.0%2B-blue)
![API](https://img.shields.io/badge/API-24%2B-brightgreen)
![Build](https://img.shields.io/badge/Build-GitHub%20Actions%20CI%2FCD-success)

---

## 📱 Asosiy Imkoniyatlar

1. **Xarajatlar bo'limi:**
   - Jami balans, haftalik va oylik umumiy xarajatlar.
   - Ko'z ikonkasini bosish orqali balansni yashirish / ko'rsatish (`••••••• so'm`).
   - Xarajatlarni toifalar (Oziq-ovqat, Transport, Uy-joy, Kiyim-kechak va h.k.) bo'yicha tezkor qo'shish.
   - Oxirgi xarajatlar ro'yxati, tahrirlash va o'chirish imkoniyati.
   - Haftalik xarajat limiti va limit oshib ketganda ogohlantirish indikatori.

2. **Daromadlar bo'limi:**
   - Oylik jami daromad ko'rsatkichi va o'sish dinamikasi.
   - Daromad manbalari (Oylik maosh, Frilans, Savdo, Depozit va h.k.) bo'yicha hisob yuritish.
   - To'lov hisob turi (Karta, Naqd pul, Payme/Click va h.k.).
   - Daromadlarni tahrirlash va o'chirish.

3. **Moliyaviy tarix va tahlil:**
   - Kunlik, Haftalik, Oylik va Yillik kesimdagi moliyaviy hisobotlar.
   - Haftaning har bir kuni (Dushanba – Yakshanba) bo'yicha interaktiv ustunli diagramma (Bar Chart).
   - Kategoriyalar bo'yicha xarajatlar taqsimoti (foiz va summa ko'rinishida progress barlar).
   - Kunlar kesimida guruhlangan kundalik operatsiyalar.

4. **Qarzlar (Telemetriya & Nazorat):**
   - "Men bergan" (Olinadigan / Receivable) va "Men olgan" (Qaytariladigan / Payable) qarzlar hisobi.
   - Holat belgilari: *Bugun to'lash kerak*, *Muddati o'tgan*, *Rejali*, *To'langan*.
   - Qarz egasiga bir bosishda to'g'ridan-to'g'ri telefon qilish (Qo'ng'iroq) tugmasi.
   - Qarzni bir bosishda to'langan deb belgilash va arxivlash.
   - Fon eslatmalari: `WorkManager` orqali muddati kelgan qarzlar haqida bildirishnoma yuborish.

---

## 🛠 Texnologiyalar

- **Dasturlash tili:** Java (Android Native)
- **Min SDK:** 24 (Android 7.0 Nougat)
- **Target SDK:** 34 (Android 14)
- **Arxitektura:** Clean Repository Pattern & SQLiteOpenHelper
- **Dizayn:** Material Components, Dynamic Dark / Light theme moslashuvi
- **Fon vazifalari:** AndroidX WorkManager
- **Avtomatlashtirish:** GitHub Actions CI/CD avtomatik APK build

---

## 🚀 GitHub Actions orqali APK yig'ish (Build)

Har bir `push` yoki `pull_request` amalga oshirilganda GitHub Actions serverlarida avtomatik ravishda:
- Debug APK (`app-debug.apk`)
- Release APK (`app-release.apk`)

yig'iladi va repository ning **Actions** bo'limida yuklab olish uchun tayyor bo'ladi.

### APK fayllarni yuklab olish:
1. GitHub repozitoriyangizga kiring: [https://github.com/Orifjon5583/jamshid.apk](https://github.com/Orifjon5583/jamshid.apk)
2. **Actions** menyusiga o'ting.
3. Eng so'nggi `Build Android APK` ishga tushuvini (workflow run) bosing.
4. Pastki qismdagi **Artifacts** bo'limidan `HaftalikXarajat-Release-APK` yoki `HaftalikXarajat-Debug-APK` ni yuklab oling va telefonga o'rnating!

---

## 💻 Mahalliy kompyuterda yig'ish (Local Build)

```bash
# Debug APK yig'ish
./gradlew assembleDebug

# Imzolangan Release APK yig'ish
./gradlew assembleRelease

# Testlarni yurgazish
./gradlew test
```
