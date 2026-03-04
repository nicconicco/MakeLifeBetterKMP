import SwiftUI
import FirebaseCore
import UserNotifications
import StripePaymentSheet

@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var delegate
    @State private var themeManager = ThemeManager()

    var body: some Scene {
        WindowGroup {
            AppView()
                .environment(themeManager)
        }
    }
}

class AppDelegate: NSObject, UIApplicationDelegate, UNUserNotificationCenterDelegate {
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil) -> Bool {
        FirebaseApp.configure()
        UNUserNotificationCenter.current().delegate = self

        // Initialize Stripe
        STPAPIClient.shared.publishableKey = "pk_test_51T2Z3YRslXggpQQoHnqTXWQJGZLZnS6cgvBg3i2SzjxSeDH1ZU74gTrAIwEVFPfr1FSjzIZ2uoEET4rsd8hTWvit00GJcrsmKR"

        return true
    }

    // Handle notification when app is in foreground
    func userNotificationCenter(_ center: UNUserNotificationCenter, willPresent notification: UNNotification, withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void) {
        completionHandler([.banner, .sound, .badge])
    }

    // Handle notification tap
    func userNotificationCenter(_ center: UNUserNotificationCenter, didReceive response: UNNotificationResponse, withCompletionHandler completionHandler: @escaping () -> Void) {
        completionHandler()
    }
}
