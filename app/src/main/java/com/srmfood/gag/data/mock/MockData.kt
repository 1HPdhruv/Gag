package com.srmfood.gag.data.mock

import com.srmfood.gag.domain.model.Cart
import com.srmfood.gag.domain.model.CartItem
import com.srmfood.gag.domain.model.CustomizationOption
import com.srmfood.gag.domain.model.FoodCategory
import com.srmfood.gag.domain.model.FoodCustomization
import com.srmfood.gag.domain.model.FoodItem
import com.srmfood.gag.domain.model.GagNotification
import com.srmfood.gag.domain.model.NotificationType
import com.srmfood.gag.domain.model.OperatingHours
import com.srmfood.gag.domain.model.Order
import com.srmfood.gag.domain.model.OrderItem
import com.srmfood.gag.domain.model.OrderStatus
import com.srmfood.gag.domain.model.Outlet
import com.srmfood.gag.domain.model.OutletLocation
import com.srmfood.gag.domain.model.PaymentMethod
import com.srmfood.gag.domain.model.PaymentStatus
import com.srmfood.gag.domain.model.PickupSlot
import com.srmfood.gag.domain.model.SlotStatus
import com.srmfood.gag.domain.model.User
import com.srmfood.gag.domain.model.UserRole

/**
 * Static mock data for development/testing without a backend.
 * 5 outlets, 22 food items, multiple categories, students, orders, slots.
 */
object MockData {

    // ─── Mock Users ───────────────────────────────────────────────
    val mockStudent = User(
        id = "student-001",
        name = "Dhruva Mishra",
        email = "student@srmist.edu.in",
        phone = "9876543210",
        role = UserRole.STUDENT,
        profileImageUrl = null,
        registrationNumber = "RA2211003010001",
        isActive = true,
        createdAt = "2024-01-01T00:00:00Z"
    )

    val mockVendor = User(
        id = "vendor-001",
        name = "Ravi Kumar",
        email = "vendor@srm.ac.in",
        phone = "9876543211",
        role = UserRole.VENDOR,
        profileImageUrl = null,
        registrationNumber = null,
        isActive = true,
        createdAt = "2024-01-01T00:00:00Z"
    )

    val mockAdmin = User(
        id = "admin-001",
        name = "Admin SRM",
        email = "admin@srm.ac.in",
        phone = "9876543212",
        role = UserRole.ADMIN,
        profileImageUrl = null,
        registrationNumber = null,
        isActive = true,
        createdAt = "2024-01-01T00:00:00Z"
    )

    // ─── Mock Credentials ─────────────────────────────────────────
    val credentials = mapOf(
        "student@srmist.edu.in" to ("student123" to mockStudent),
        "vendor@srm.ac.in" to ("vendor123" to mockVendor),
        "admin@srm.ac.in" to ("admin123" to mockAdmin)
    )

    // ─── Mock Outlets ─────────────────────────────────────────────
    val outlets = listOf(
        Outlet(
            id = "outlet-001",
            name = "Main Canteen",
            description = "The largest food court at SRM KTR with a wide variety of South Indian, North Indian and Chinese cuisine.",
            imageUrl = "https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=600",
            location = OutletLocation("TP Main Building", "Ground Floor", "Near main entrance", 12.8231, 80.0446),
            isOpen = true,
            operatingHours = OperatingHours("07:30", "21:00", listOf("Mon","Tue","Wed","Thu","Fri","Sat")),
            currentQueueSize = 8,
            estimatedWaitMinutes = 12,
            categories = listOf("Meals", "South Indian", "Chinese", "Beverages"),
            rating = 4.2,
            totalReviews = 342,
            vendorId = "vendor-001",
            isActive = true,
            phone = "044-47432000"
        ),
        Outlet(
            id = "outlet-002",
            name = "Pizza & Burger Hub",
            description = "Fast food favourite with burgers, pizzas, wraps and loaded fries. Student-friendly prices!",
            imageUrl = "https://images.unsplash.com/photo-1571091718767-18b5b1457add?w=600",
            location = OutletLocation("Tech Park", "1st Floor", "Opposite the library", 12.8234, 80.0449),
            isOpen = true,
            operatingHours = OperatingHours("10:00", "22:00", listOf("Mon","Tue","Wed","Thu","Fri","Sat","Sun")),
            currentQueueSize = 3,
            estimatedWaitMinutes = 7,
            categories = listOf("Fast Food", "Pizza", "Beverages"),
            rating = 4.5,
            totalReviews = 520,
            vendorId = "vendor-002",
            isActive = true,
            phone = null
        ),
        Outlet(
            id = "outlet-003",
            name = "Momo Junction",
            description = "Authentic Tibetan & North-East Indian style momos, soups and noodles.",
            imageUrl = "https://images.unsplash.com/photo-1563245372-f21724e3856d?w=600",
            location = OutletLocation("Hostel Block A", "Ground Floor", "Near hostel A entrance", 12.8219, 80.0440),
            isOpen = true,
            operatingHours = OperatingHours("11:00", "21:00", listOf("Mon","Tue","Wed","Thu","Fri","Sat")),
            currentQueueSize = 18,
            estimatedWaitMinutes = 22,
            categories = listOf("Chinese", "Snacks", "Beverages"),
            rating = 4.7,
            totalReviews = 890,
            vendorId = "vendor-003",
            isActive = true,
            phone = null
        ),
        Outlet(
            id = "outlet-004",
            name = "Beverages & Bites",
            description = "Refreshing drinks, juices, milkshakes, chai, coffee and quick snacks.",
            imageUrl = "https://images.unsplash.com/photo-1495474472287-4d71bcdd2085?w=600",
            location = OutletLocation("Engineering Block", "2nd Floor", "Near the elevator", 12.8238, 80.0452),
            isOpen = true,
            operatingHours = OperatingHours("08:00", "20:00", listOf("Mon","Tue","Wed","Thu","Fri")),
            currentQueueSize = 2,
            estimatedWaitMinutes = 4,
            categories = listOf("Beverages", "Snacks"),
            rating = 4.0,
            totalReviews = 210,
            vendorId = "vendor-004",
            isActive = true,
            phone = null
        ),
        Outlet(
            id = "outlet-005",
            name = "South Spice Kitchen",
            description = "Authentic Tamil and Kerala meals — rice plates, dosas, idlis, sambar & more.",
            imageUrl = "https://images.unsplash.com/photo-1601050690597-df0568f70950?w=600",
            location = OutletLocation("MBA Block", "Ground Floor", "Behind the MBA building", 12.8226, 80.0444),
            isOpen = false,
            operatingHours = OperatingHours("07:30", "15:00", listOf("Mon","Tue","Wed","Thu","Fri")),
            currentQueueSize = 0,
            estimatedWaitMinutes = 0,
            categories = listOf("Meals", "South Indian", "Beverages"),
            rating = 4.4,
            totalReviews = 456,
            vendorId = "vendor-005",
            isActive = true,
            phone = "044-47432001"
        )
    )

    // ─── Mock Food Categories ─────────────────────────────────────
    val categories = listOf(
        FoodCategory("cat-1", "Meals", "🍛", null),
        FoodCategory("cat-2", "Fast Food", "🍔", null),
        FoodCategory("cat-3", "Beverages", "🥤", null),
        FoodCategory("cat-4", "Pizza", "🍕", null),
        FoodCategory("cat-5", "Snacks", "🥪", null),
        FoodCategory("cat-6", "Chinese", "🍜", null),
        FoodCategory("cat-7", "South Indian", "🥘", null),
        FoodCategory("cat-8", "Desserts", "🍰", null)
    )

    // ─── Mock Food Items ──────────────────────────────────────────
    val spiceLevelCustomization = FoodCustomization(
        id = "cust-1", name = "Spice Level",
        options = listOf(
            CustomizationOption("opt-1", "Mild", 0.0),
            CustomizationOption("opt-2", "Medium", 0.0),
            CustomizationOption("opt-3", "Hot", 0.0),
            CustomizationOption("opt-4", "Extra Hot", 0.0)
        ),
        isRequired = false, maxSelections = 1
    )

    val foodItems = listOf(
        // Main Canteen
        FoodItem("food-001", "Chicken Biryani", "Aromatic basmati rice cooked with tender chicken and spices.", "https://images.unsplash.com/photo-1589302168068-964664d93dc0?w=400",
            price = 80.0, outletId = "outlet-001", outletName = "Main Canteen", category = "Meals",
            isVeg = false, isAvailable = true, prepTimeMinutes = 12, rating = 4.5, totalReviews = 120,
            ingredients = listOf("Basmati rice", "Chicken", "Onion", "Spices"),
            customizations = listOf(spiceLevelCustomization), tags = listOf("popular", "non-veg"),
            calories = 550, isPopular = true, isRecommended = true),
        FoodItem("food-002", "Veg Fried Rice", "Wok-tossed rice with fresh vegetables.", "https://images.unsplash.com/photo-1603133872878-684f208fb84b?w=400",
            price = 60.0, outletId = "outlet-001", outletName = "Main Canteen", category = "Chinese",
            isVeg = true, isAvailable = true, prepTimeMinutes = 8, rating = 4.1, totalReviews = 88,
            ingredients = listOf("Rice", "Mixed vegetables", "Soy sauce", "Spring onion"),
            customizations = emptyList(), tags = listOf("veg"), calories = 380, isPopular = false, isRecommended = false),
        FoodItem("food-003", "Paneer Butter Masala + Rice", "Rich tomato-based curry with soft paneer.", "https://images.unsplash.com/photo-1565557623262-b51c2513a641?w=400",
            price = 75.0, outletId = "outlet-001", outletName = "Main Canteen", category = "Meals",
            isVeg = true, isAvailable = true, prepTimeMinutes = 10, rating = 4.3, totalReviews = 96,
            ingredients = listOf("Paneer", "Tomato", "Cream", "Spices", "Rice"),
            customizations = listOf(spiceLevelCustomization), tags = listOf("veg"), calories = 490, isPopular = true, isRecommended = false),
        FoodItem("food-004", "Egg Fried Rice", "Classic fried rice with scrambled eggs.", "https://images.unsplash.com/photo-1603133872878-684f208fb84b?w=400",
            price = 55.0, outletId = "outlet-001", outletName = "Main Canteen", category = "Chinese",
            isVeg = false, isAvailable = true, prepTimeMinutes = 8, rating = 4.0, totalReviews = 65,
            ingredients = listOf("Rice", "Egg", "Spring onion", "Soy sauce"),
            customizations = emptyList(), tags = listOf("egg"), calories = 410, isPopular = false, isRecommended = false),
        FoodItem("food-005", "Masala Chai", "Strong, spiced Indian tea.", null,
            price = 15.0, outletId = "outlet-001", outletName = "Main Canteen", category = "Beverages",
            isVeg = true, isAvailable = true, prepTimeMinutes = 3, rating = 4.6, totalReviews = 200,
            ingredients = listOf("Tea", "Milk", "Ginger", "Cardamom"),
            customizations = emptyList(), tags = listOf("beverage", "popular"), calories = 90, isPopular = true, isRecommended = false),

        // Pizza & Burger Hub
        FoodItem("food-006", "Chicken Burger", "Crispy fried chicken patty with lettuce, tomato, and special sauce.", "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=400",
            price = 70.0, outletId = "outlet-002", outletName = "Pizza & Burger Hub", category = "Fast Food",
            isVeg = false, isAvailable = true, prepTimeMinutes = 8, rating = 4.6, totalReviews = 310,
            ingredients = listOf("Chicken patty", "Lettuce", "Tomato", "Cheese", "Burger bun"),
            customizations = emptyList(), tags = listOf("popular", "non-veg"), calories = 520, isPopular = true, isRecommended = true),
        FoodItem("food-007", "Veg Burger", "Crispy aloo tikki patty with fresh veggies.", "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=400",
            price = 55.0, outletId = "outlet-002", outletName = "Pizza & Burger Hub", category = "Fast Food",
            isVeg = true, isAvailable = true, prepTimeMinutes = 7, rating = 4.2, totalReviews = 165,
            ingredients = listOf("Aloo tikki", "Lettuce", "Tomato", "Sauce", "Burger bun"),
            customizations = emptyList(), tags = listOf("veg"), calories = 420, isPopular = false, isRecommended = false),
        FoodItem("food-008", "Margherita Pizza (7\")", "Classic tomato sauce and mozzarella.", "https://images.unsplash.com/photo-1574071318508-1cdbab80d002?w=400",
            price = 90.0, outletId = "outlet-002", outletName = "Pizza & Burger Hub", category = "Pizza",
            isVeg = true, isAvailable = true, prepTimeMinutes = 15, rating = 4.4, totalReviews = 200,
            ingredients = listOf("Dough", "Tomato sauce", "Mozzarella", "Basil"),
            customizations = emptyList(), tags = listOf("veg", "pizza"), calories = 600, isPopular = false, isRecommended = true),
        FoodItem("food-009", "Chicken Pizza (7\")", "Grilled chicken with peppers and cheese.", "https://images.unsplash.com/photo-1544982503-9f984c14501a?w=400",
            price = 110.0, outletId = "outlet-002", outletName = "Pizza & Burger Hub", category = "Pizza",
            isVeg = false, isAvailable = true, prepTimeMinutes = 15, rating = 4.7, totalReviews = 280,
            ingredients = listOf("Dough", "Chicken", "Mozzarella", "Peppers"),
            customizations = emptyList(), tags = listOf("non-veg", "pizza", "popular"), calories = 680, isPopular = true, isRecommended = false),
        FoodItem("food-010", "Loaded Fries", "Crispy fries with cheese sauce and jalapeños.", "https://images.unsplash.com/photo-1518013431117-eb1465fa5752?w=400",
            price = 45.0, outletId = "outlet-002", outletName = "Pizza & Burger Hub", category = "Snacks",
            isVeg = true, isAvailable = true, prepTimeMinutes = 6, rating = 4.3, totalReviews = 190,
            ingredients = listOf("Potato", "Cheese sauce", "Jalapeños"),
            customizations = emptyList(), tags = listOf("veg", "snack"), calories = 380, isPopular = false, isRecommended = false),

        // Momo Junction
        FoodItem("food-011", "Steam Momos (8 pcs)", "Soft steamed dumplings filled with spiced vegetables.", "https://images.unsplash.com/photo-1563245372-f21724e3856d?w=400",
            price = 50.0, outletId = "outlet-003", outletName = "Momo Junction", category = "Chinese",
            isVeg = true, isAvailable = true, prepTimeMinutes = 12, rating = 4.8, totalReviews = 560,
            ingredients = listOf("Flour", "Cabbage", "Carrot", "Onion", "Ginger"),
            customizations = listOf(spiceLevelCustomization), tags = listOf("veg", "popular"), calories = 280, isPopular = true, isRecommended = true),
        FoodItem("food-012", "Chicken Momos (8 pcs)", "Juicy chicken-filled steamed momos.", "https://images.unsplash.com/photo-1563245372-f21724e3856d?w=400",
            price = 65.0, outletId = "outlet-003", outletName = "Momo Junction", category = "Chinese",
            isVeg = false, isAvailable = true, prepTimeMinutes = 14, rating = 4.9, totalReviews = 720,
            ingredients = listOf("Flour", "Chicken", "Onion", "Ginger", "Spices"),
            customizations = listOf(spiceLevelCustomization), tags = listOf("non-veg", "popular"), calories = 340, isPopular = true, isRecommended = true),
        FoodItem("food-013", "Fried Momos (6 pcs)", "Crispy fried momos with spicy dipping sauce.", "https://images.unsplash.com/photo-1563245372-f21724e3856d?w=400",
            price = 60.0, outletId = "outlet-003", outletName = "Momo Junction", category = "Chinese",
            isVeg = true, isAvailable = true, prepTimeMinutes = 15, rating = 4.6, totalReviews = 380,
            ingredients = listOf("Flour", "Mixed vegetables", "Oil", "Spices"),
            customizations = emptyList(), tags = listOf("veg"), calories = 350, isPopular = false, isRecommended = false),
        FoodItem("food-014", "Thukpa Noodle Soup", "Hearty Tibetan noodle soup with vegetables.", "https://images.unsplash.com/photo-1569718212165-3a8278d5f624?w=400",
            price = 70.0, outletId = "outlet-003", outletName = "Momo Junction", category = "Chinese",
            isVeg = false, isAvailable = false, prepTimeMinutes = 15, rating = 4.4, totalReviews = 145,
            ingredients = listOf("Noodles", "Chicken broth", "Vegetables", "Egg"),
            customizations = emptyList(), tags = listOf("non-veg", "soup"), calories = 310, isPopular = false, isRecommended = false),

        // Beverages & Bites
        FoodItem("food-015", "Mango Lassi", "Thick, creamy mango yoghurt drink.", "https://images.unsplash.com/photo-1553361371-9b22f78e8b1d?w=400",
            price = 40.0, outletId = "outlet-004", outletName = "Beverages & Bites", category = "Beverages",
            isVeg = true, isAvailable = true, prepTimeMinutes = 3, rating = 4.5, totalReviews = 140,
            ingredients = listOf("Mango", "Yoghurt", "Sugar", "Cardamom"),
            customizations = emptyList(), tags = listOf("veg", "beverage"), calories = 180, isPopular = false, isRecommended = false),
        FoodItem("food-016", "Cold Coffee", "Chilled blended coffee with milk.", "https://images.unsplash.com/photo-1461023058943-07fcbe16d735?w=400",
            price = 50.0, outletId = "outlet-004", outletName = "Beverages & Bites", category = "Beverages",
            isVeg = true, isAvailable = true, prepTimeMinutes = 3, rating = 4.3, totalReviews = 98,
            ingredients = listOf("Coffee", "Milk", "Sugar", "Ice cream"),
            customizations = emptyList(), tags = listOf("veg", "beverage", "popular"), calories = 220, isPopular = true, isRecommended = false),
        FoodItem("food-017", "Samosa (2 pcs)", "Crispy fried pastry filled with spiced potato.", null,
            price = 20.0, outletId = "outlet-004", outletName = "Beverages & Bites", category = "Snacks",
            isVeg = true, isAvailable = true, prepTimeMinutes = 2, rating = 4.0, totalReviews = 88,
            ingredients = listOf("Flour", "Potato", "Peas", "Spices"),
            customizations = emptyList(), tags = listOf("veg", "snack"), calories = 160, isPopular = false, isRecommended = false),

        // South Spice Kitchen
        FoodItem("food-018", "Full Meals (Thali)", "Complete South Indian lunch — rice, sambar, rasam, kootu, appalam, payasam.", "https://images.unsplash.com/photo-1567188040759-fb8a883dc6d8?w=400",
            price = 90.0, outletId = "outlet-005", outletName = "South Spice Kitchen", category = "Meals",
            isVeg = true, isAvailable = false, prepTimeMinutes = 5, rating = 4.6, totalReviews = 320,
            ingredients = listOf("Rice", "Sambar", "Rasam", "Kootu", "Appalam", "Payasam"),
            customizations = emptyList(), tags = listOf("veg", "popular"), calories = 700, isPopular = true, isRecommended = true),
        FoodItem("food-019", "Masala Dosa", "Crispy rice crepe filled with spiced potato masala.", "https://images.unsplash.com/photo-1630383249896-424e482df921?w=400",
            price = 50.0, outletId = "outlet-005", outletName = "South Spice Kitchen", category = "South Indian",
            isVeg = true, isAvailable = false, prepTimeMinutes = 8, rating = 4.5, totalReviews = 240,
            ingredients = listOf("Rice batter", "Potato", "Onion", "Spices"),
            customizations = emptyList(), tags = listOf("veg", "south indian"), calories = 350, isPopular = false, isRecommended = false),
        FoodItem("food-020", "Idli Sambar (4 pcs)", "Soft steamed rice cakes with sambar and chutney.", "https://images.unsplash.com/photo-1547592180-85f173990554?w=400",
            price = 35.0, outletId = "outlet-005", outletName = "South Spice Kitchen", category = "South Indian",
            isVeg = true, isAvailable = false, prepTimeMinutes = 5, rating = 4.3, totalReviews = 190,
            ingredients = listOf("Rice", "Urad dal", "Sambar", "Coconut chutney"),
            customizations = emptyList(), tags = listOf("veg", "south indian", "breakfast"), calories = 240, isPopular = false, isRecommended = false),
        FoodItem("food-021", "Filter Coffee", "Traditional South Indian decoction coffee.", null,
            price = 20.0, outletId = "outlet-005", outletName = "South Spice Kitchen", category = "Beverages",
            isVeg = true, isAvailable = false, prepTimeMinutes = 3, rating = 4.7, totalReviews = 280,
            ingredients = listOf("Coffee decoction", "Milk", "Sugar"),
            customizations = emptyList(), tags = listOf("veg", "beverage", "south indian"), calories = 95, isPopular = false, isRecommended = false),
        FoodItem("food-022", "Chicken Noodles", "Stir-fried noodles with chicken and vegetables.", "https://images.unsplash.com/photo-1569718212165-3a8278d5f624?w=400",
            price = 75.0, outletId = "outlet-001", outletName = "Main Canteen", category = "Chinese",
            isVeg = false, isAvailable = true, prepTimeMinutes = 10, rating = 4.2, totalReviews = 75,
            ingredients = listOf("Noodles", "Chicken", "Mixed vegetables", "Soy sauce"),
            customizations = listOf(spiceLevelCustomization), tags = listOf("non-veg"), calories = 460, isPopular = false, isRecommended = false)
    )

    // ─── Mock Pickup Slots ────────────────────────────────────────
    fun generatePickupSlots(outletId: String, date: String): List<PickupSlot> = listOf(
        PickupSlot("slot-1", outletId, "12:00", "12:10", date, 20, 14, SlotStatus.AVAILABLE),
        PickupSlot("slot-2", outletId, "12:10", "12:20", date, 20, 18, SlotStatus.LIMITED),
        PickupSlot("slot-3", outletId, "12:20", "12:30", date, 20, 20, SlotStatus.FULL),
        PickupSlot("slot-4", outletId, "12:30", "12:40", date, 20, 8, SlotStatus.AVAILABLE),
        PickupSlot("slot-5", outletId, "12:40", "12:50", date, 20, 2, SlotStatus.AVAILABLE),
        PickupSlot("slot-6", outletId, "12:50", "13:00", date, 20, 0, SlotStatus.AVAILABLE),
        PickupSlot("slot-7", outletId, "13:00", "13:10", date, 20, 17, SlotStatus.LIMITED),
        PickupSlot("slot-8", outletId, "13:10", "13:20", date, 20, 5, SlotStatus.AVAILABLE),
        PickupSlot("slot-9", outletId, "17:00", "17:10", date, 20, 0, SlotStatus.AVAILABLE),
        PickupSlot("slot-10", outletId, "17:10", "17:20", date, 20, 0, SlotStatus.AVAILABLE)
    )

    // ─── Mock Orders ──────────────────────────────────────────────
    val mockOrders = mutableListOf(
        Order(
            id = "order-001", orderNumber = "#A482",
            userId = "student-001", vendorId = "vendor-001", outletId = "outlet-001", outletName = "Main Canteen",
            items = listOf(
                OrderItem("oi-1", "food-006", "Chicken Burger", null, 2, 70.0, 140.0, emptyList(), false),
                OrderItem("oi-2", "food-010", "Loaded Fries", null, 1, 45.0, 45.0, emptyList(), true)
            ),
            subtotal = 185.0, tax = 9.25, total = 194.25,
            status = OrderStatus.PREPARING,
            pickupSlot = PickupSlot("slot-6", "outlet-001", "12:50", "13:00", "2024-01-15", 20, 5, SlotStatus.AVAILABLE),
            estimatedPrepMinutes = 12, actualPrepMinutes = null,
            createdAt = "2024-01-15T12:30:00Z", placedAt = "2024-01-15T12:31:00Z",
            acceptedAt = "2024-01-15T12:33:00Z", preparingAt = "2024-01-15T12:35:00Z",
            readyAt = null, pickedUpAt = null, cancelledAt = null, cancellationReason = null,
            paymentStatus = PaymentStatus.PENDING, paymentMethod = PaymentMethod.PAY_AT_COUNTER,
            specialInstructions = "Less spice please", qrToken = null
        ),
        Order(
            id = "order-002", orderNumber = "#A481",
            userId = "student-001", vendorId = "vendor-003", outletId = "outlet-003", outletName = "Momo Junction",
            items = listOf(
                OrderItem("oi-3", "food-012", "Chicken Momos (8 pcs)", null, 1, 65.0, 65.0, listOf("Spice: Hot"), false)
            ),
            subtotal = 65.0, tax = 3.25, total = 68.25,
            status = OrderStatus.PICKED_UP,
            pickupSlot = PickupSlot("slot-3", "outlet-003", "12:20", "12:30", "2024-01-14", 20, 20, SlotStatus.FULL),
            estimatedPrepMinutes = 14, actualPrepMinutes = 13,
            createdAt = "2024-01-14T12:10:00Z", placedAt = "2024-01-14T12:11:00Z",
            acceptedAt = "2024-01-14T12:12:00Z", preparingAt = "2024-01-14T12:13:00Z",
            readyAt = "2024-01-14T12:26:00Z", pickedUpAt = "2024-01-14T12:28:00Z",
            cancelledAt = null, cancellationReason = null,
            paymentStatus = PaymentStatus.PAID, paymentMethod = PaymentMethod.PAY_AT_COUNTER,
            specialInstructions = null, qrToken = null
        )
    )

    // ─── Mock Notifications ───────────────────────────────────────
    val mockNotifications = listOf(
        GagNotification("notif-1", "Order Ready! 🎉", "Your Chicken Momos are ready for pickup at Momo Junction", NotificationType.ORDER_READY, "order-002", true, "2024-01-14T12:26:00Z", "gag://orders/order-002"),
        GagNotification("notif-2", "Order Accepted ✅", "Main Canteen accepted your order #A482", NotificationType.ORDER_ACCEPTED, "order-001", false, "2024-01-15T12:33:00Z", "gag://orders/order-001"),
        GagNotification("notif-3", "Pickup Reminder ⏰", "Your order #A482 pickup slot starts in 10 minutes!", NotificationType.PICKUP_REMINDER, "order-001", false, "2024-01-15T12:40:00Z", "gag://orders/order-001")
    )
}
