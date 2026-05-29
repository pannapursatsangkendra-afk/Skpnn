package com.example.ui

object Translations {
    var currentLanguage = "Bengali" // Default to Bengali as indicated, but user can toggle anytime

    private val translations = mapOf(
        "app_title" to Pair("SKPNN Temple Management System", "SKPNN মন্দির পরিচালনা ব্যবস্থা"),
        "dashboard" to Pair("Dashboard", "ড্যাশবোর্ড"),
        "monthly_istravrity" to Pair("Monthly Istravrity", "মাসিক ইষ্টভৃতি"),
        "skpnn" to Pair("S.K.P.N.N", "এস.কে.পি.এন.এন"),
        "mandhir" to Pair("Mandhir", "মন্দির"),
        "expenses" to Pair("Expenses", "খরচ বিবরণী"),
        "upajona" to Pair("Upajona", "উপযাজনা"),
        "user_control" to Pair("User Control", "ব্যবহারকারী নিয়ন্ত্রণ"),
        "accounting" to Pair("Accounting", "অ্যাকাউন্টিং"),
        
        // Buttons / Actions
        "save" to Pair("Save", "সংরক্ষণ করুন"),
        "edit" to Pair("Edit", "সম্পাদনা"),
        "delete" to Pair("Delete", "ডিলিট"),
        "print" to Pair("Print", "প্রিন্ট"),
        "pdf_download" to Pair("PDF Download", "পিডিএফ ডাউনলোড"),
        "share_whatsapp" to Pair("Share via WhatsApp", "হোয়াটসঅ্যাপে শেয়ার"),
        "bulk_select" to Pair("Bulk Select", "বাল্ক নির্বাচন"),
        "add_new" to Pair("Add New", "নতুন যোগ করুন"),
        "cancel" to Pair("Cancel", "বাতিল করুন"),
        "submit" to Pair("Submit", "জমা দিন"),
        "block" to Pair("Block", "ব্লক"),
        "unblock" to Pair("Active", "সক্রিয়"),
        "approve" to Pair("Approve", "অনুমোদন করুন"),
        
        // Roles & Status
        "user" to Pair("User", "ব্যবহারকারী"),
        "admin" to Pair("Admin", "অ্যাডমিন"),
        "super_admin" to Pair("Super Admin", "সুপার অ্যাডমিন"),
        "active" to Pair("Active", "সক্রিয়"),
        "inactive" to Pair("Inactive", "নিষ্ক্রিয়"),
        "blocked" to Pair("Blocked", "ব্লকড"),
        
        // Fields
        "full_name" to Pair("Full Name", "পূর্ণ নাম"),
        "address" to Pair("Address", "ঠিকানা"),
        "mobile" to Pair("Mobile Number", "মোবাইল নম্বর"),
        "pin_code" to Pair("PIN Code", "পিন কোড"),
        "family_code" to Pair("Family Code", "ফ্যামিলি কোড"),
        "amount" to Pair("Amount", "টাকার পরিমাণ"),
        "date" to Pair("Date", "তারিখ"),
        "serial_no" to Pair("Serial Number", "সিরিয়াল নম্বর"),
        "lot_no" to Pair("Lot Number", "লট নম্বর"),
        "pan_no" to Pair("PAN Number", "প্যান নম্বর"),
        "remarks" to Pair("Remarks", "মন্তব্য"),
        "ritwick_name" to Pair("Ritwick's Name in full", "ঋত্বিক-এর পূর্ণ নাম"),
        "phone_mobile" to Pair("Phone/Mobile (If any)", "ফোন/মোবাইল (যদি থাকে)"),
        "cash_code" to Pair("Cash / Code", "ক্যাশ / কোড"),
        "status" to Pair("Status", "অবস্থা"),
        "promise_amount" to Pair("Promise Amount", "প্রতিজ্ঞাত অর্থ"),
        "due_amount" to Pair("Due Amount", "বকেয়া অর্থ"),
        
        // Istravrity Fields
        "swastyayani" to Pair("Swastyayani", "স্বস্ত্যয়নী"),
        "istabhriti" to Pair("Istavrity", "ইষ্টভৃতি"),
        "acharyabhriti" to Pair("Acharyavrity", "আচার্যভৃতি"),
        "dakshina" to Pair("Dakshina", "দক্ষিণা"),
        "sangathani" to Pair("Sangathani", "সংগঠনী"),
        "ritwicki" to Pair("Ritwicki", "ঋত্বিকী"),
        "pronami" to Pair("Pronami", "প্রণামী"),
        "anandabazar" to Pair("Anandabazar", "আনন্দবাজার"),
        "srimandir_corpus" to Pair("Srimandir (Corpus)", "শ্রীমন্দির (করপাস)"),
        "parivrity" to Pair("Parivrity", "পরিবৃত্তি"),
        "utsav" to Pair("Utsav with Description", "উৎসবের বিবরণ ও টাকা"),
        "utsav_amount" to Pair("Utsav Amount", "উৎসবের পরিমাণ"),
        "utsav_desc" to Pair("Utsav Description", "উৎসবের বিবরণ"),
        "arghya_token" to Pair("Arghya Deposit Token No", "অর্ঘ্য জমার টোকেন নং"),
        "total" to Pair("Total", "মোট পরিমাণ"),
        "rs" to Pair("Rs.", "টাকা"),
        "rupees_words" to Pair("Rupees in words", "কথায় টাকা"),
        
        // Dashboard Stats
        "total_collection" to Pair("Total Collection", "মোট সংগ্রহ"),
        "paid_summary" to Pair("Paid Summary", "পরিশোধিত সারসংক্ষেপ"),
        "promise_summary" to Pair("Promise Summary", "প্রতিজ্ঞাত সারসংক্ষেপ"),
        "due_summary" to Pair("Due Summary", "বকেয়া সারসংক্ষেপ"),
        "mandhir_details" to Pair("Mandhir Details", "মন্দিরের বিবরণ"),
        "skpnn_details" to Pair("SKPNN Details", "এসকেপিএনএন বিবরণ"),
        "analytics" to Pair("Analytics & Trends", "বিশ্লেষণ এবং প্রবণতা"),
        "recent_entries" to Pair("Recent Entries", "সাম্প্রতিক এন্ট্রি সমূহ"),
        "recent_expenses" to Pair("Recent Expenses", "সাম্প্রতিক খরচ বিবরণী"),
        "pending_approvals" to Pair("Pending Approvals", "অনুমোদনের অপেক্ষায়"),
        "unapproved_msg" to Pair("Your account is pending registration approval by Super Admin.", "আপনার অ্যাকাউন্টটি সুপার অ্যাডমিনের অনুমোদনের অপেক্ষায় রয়েছে।"),
        "blocked_msg" to Pair("Your account has been permanently blocked.", "আপনার অ্যাকাউন্টটি স্থায়ীভাবে ব্লক করা হয়েছে।"),

        // Login Screen
        "login_title" to Pair("Login to SKPNN System", "এসকেপিএনএন সিস্টেমে লগইন করুন"),
        "register_title" to Pair("Register New Account", "নতুন অ্যাকাউন্ট নিবন্ধন"),
        "dont_have_account" to Pair("Don't have an account? Register", "অ্যাকাউন্ট নেই? রেজিস্টার করুন"),
        "already_have_account" to Pair("Already have an account? Login", "অ্যাকাউন্ট আছে? লগইন করুন"),
        "password" to Pair("Password", "পাসওয়ার্ড"),
        "login_btn" to Pair("Login", "লগইন"),
        "register_btn" to Pair("Register", "রেজিস্টার"),
        "logout" to Pair("Logout", "লগআউট")
    )

    fun get(key: String): String {
        val pair = translations[key] ?: return key
        return if (currentLanguage == "Bengali") pair.second else pair.first
    }
}
