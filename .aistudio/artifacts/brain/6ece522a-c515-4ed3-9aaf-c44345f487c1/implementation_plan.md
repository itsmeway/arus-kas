# Rencana Implementasi: Penghapusan Google Login & Format Otomatis Rupiah (Rp dan Titik Ribuan)

## Ringkasan Proyek
Permintaan pengguna mencakup dua tujuan utama:
1. **Menghapus fitur Google Login**: Menghilangkan tombol login/logout, modal autentikasi Google, dan loop `CredentialManager` agar aplikasi menjadi sangat ringan, instan dibuka, dan 100% beroperasi secara lokal tanpa hambatan akun.
2. **Format Otomatis Nominal Rupiah**: Menerapkan format mata uang standar Indonesia (`Rp ` dan titik pemisah ribuan, misal `Rp 50.000` atau `Rp 1.500.000`) pada **semua kolom input nominal** di seluruh aplikasi secara real-time saat pengguna mengetik angka, sehingga nyaman dibaca dan mencegah kesalahan hitung.

---

## 1. Analisis & Rincian Perubahan

### A. Penghapusan Google Login (Aplikasi Ringan & Bersih)
- **Top Bar Beranda (`BerandaExpenseDashboard.kt`)**: Menghilangkan tombol `Masuk` / `Keluar` di pojok kanan atas sehingga Top Bar hanya memuat Logo, Brand, Chip PRO Trial, Panduan, dan Setelan.
- **Navigasi Utama (`MainActivity.kt`)**:
  - Menghapus pemanggilan `attemptAutoSignIn`, `signOutUser`, dan flow `CredentialManager`.
  - Menghapus `AuthModalBottomSheet` dan state `showSignInSheet`.
  - Menjalankan `MainArusKasScreen` langsung dalam mode lokal super cepat tanpa perlu memuat listener auth Firebase.
- **Penyimpanan Data**: Tetap 100% offline-first dan privat di dalam perangkat pengguna, dengan opsi pencadangan dokumen tabel PDF modern yang dapat diekspor kapan saja.

### B. Pemformatan Otomatis Nominal (`RupiahVisualTransformation` / Real-Time Formatter)
Semua kolom input nominal di aplikasi akan menggunakan pemformat visual atau parser otomatis:
- Pengguna cukup mengetik angka murni di keyboard angka (misal: `75000`).
- Tampilan teks di dalam kolom input secara instan menampilkan: **`Rp 75.000`**.
- Jika diketik `1500000`, otomatis tampil: **`Rp 1.500.000`**.
- Diterapkan secara merata di:
  1. **Kotak Catat Kilat Beranda** (Pengeluaran, Pemasukan, dan Pengeluaran Darurat).
  2. **Dialog Setor Celengan Target** (`DepositGoalDialog`).
  3. **Dialog Buat Target Baru** (Target Total & Nabung Harian).
  4. **Dialog Tambah / Sesuaikan Saldo Dompet & Bank** (`WalletEditorDialog`).
  5. **Dialog Ubah Limit Kategori & Brankas Darurat** (`CategoryLimitEditorDialog` & `EmergencyFundDialog`).
  6. **Dialog Setup Awal Pengguna Baru** (`FirstTimeSetupDialog`).

---

## 2. Rencana Eksekusi Bertahap

1. **Pembuatan Helper Format Nominal (`CurrencyAmountVisualTransformation`)**:
   - Membuat `CurrencyAmountVisualTransformation` atau utilitas formatter berbasis `NumberFormat` / Regex lokal Indonesia (`Locale("in", "ID")`).
   - Menyediakan fungsi filter digit dan pemformatan instan yang mempertahankan posisi kursor dan kalkulasi akurat.
2. **Pembaruan Kolom Input di Beranda (`BerandaExpenseDashboard.kt`)**:
   - Mengintegrasikan format Rupiah pada input nominal utama dan dialog-dialog modal di dalamnya.
3. **Pembaruan Kolom Input di Dialog & Bottom Sheet (`ArusKasDialogsAndSheets.kt`)**:
   - Memperbarui seluruh `OutlinedTextField` nominal saldo bank/dompet, limit kategori, darurat, dan celengan target dengan format `Rp [titik ribuan]`.
4. **Pembersihan Kode Google Login**:
   - Menghapus UI login di `MainActivity.kt` dan `BerandaExpenseDashboard.kt`.
5. **Verifikasi & Pengujian**:
   - Kompilasi `compile_applet`.
   - Eksekusi `gradle :app:testDebugUnitTest` untuk memastikan semua pengujian unit Robolectric dan logika transaksi lokal tetap 100% lulus.
