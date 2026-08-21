# GaG - Grab & Go 🍔
*SRM KTR's Premium Food Pre-ordering Application*

GaG (Grab & Go) is a native Android application designed to eliminate long queues and crowding at SRM Institute of Science and Technology, Kattankulathur (SRM KTR) food outlets.

## Architecture & Tech Stack
- **Architecture:** Clean Architecture + MVVM + MVI-style UiState.
- **UI:** 100% Jetpack Compose (Material 3) with custom premium Design System.
- **DI:** Dagger Hilt.
- **Concurrency:** Kotlin Coroutines & Flows (StateFlow).
- **Network:** Retrofit + OkHttp (Interfaces defined, currently using MockData implementation).
- **Local DB:** Room (Entities and DAOs defined).
- **Security:** EncryptedSharedPreferences (AES256_GCM).
- **Navigation:** Jetpack Navigation Compose (Type-safe, nested graphs).

## Project Structure
- `core/`: Common UI components, Theme, Result wrappers.
- `domain/`: Models, UseCases, Repository Interfaces.
- `data/`: DTOs, APIs, DAOs, Room DB, and robust Mock Repositories.
- `feature/`: UI components organized by feature (auth, home, orders, vendor, admin).
- `navigation/`: Central NavGraph routing.

## Development & Mock Mode
The app is currently configured to run entirely in memory using robust **Mock Repositories** (`MockAuthRepository`, `MockOrderRepository`, etc.). This allows the entire UI/UX to be developed, tested, and demonstrated without needing a live backend.

### Mock Credentials
- **Student:** `student@srmist.edu.in` / `student123`
- **Vendor:** `vendor@srm.ac.in` / `vendor123`
- **Admin:** `admin@srm.ac.in` / `admin123`

## Features Implemented
- **Auth:** Role-based login (Student, Vendor, Admin) with JWT token management.
- **Home/Search:** Outlet discovery, global food search with debouncing, category filtering.
- **Cart & Checkout:** Multi-item cart, intelligent pickup slot selection (backend capacity logic mocked), payment method selection.
- **Live Order Tracking:** Pulsing UI indicators, step-by-step progress tracking, QR code generation for pickup.
- **Vendor Dashboard:** Real-time incoming order queue, accept/reject, mark as preparing/ready, and simulated QR scanning.
- **Admin Dashboard:** System-wide stats, outlet enable/disable toggle.

## Running the App
1. Open the project in Android Studio.
2. The project is fully compile-ready.
3. Build and run on an emulator or physical device (API 26+).