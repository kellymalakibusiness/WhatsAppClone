import SwiftUI

@main
struct iOSApp: App {
    init() {
        setBlurryBackgroundForTabItemRow()
        setupTabItemColors(backgroundColor: UIColor(named: "UnselectedTabView")!)
    }
    var body: some Scene {
        WindowGroup {
            RootView()
                .accentColor(Color(UIColor(resource: .selectedTabView)))
        }
    }


    // MARK: FUNCTIONS
    func setupTabItemColors(backgroundColor: UIColor){
        let appearance = UITabBarAppearance()

        // Set the color for unselected items
        appearance.stackedLayoutAppearance.normal.iconColor = backgroundColor
        appearance.stackedLayoutAppearance.normal.titleTextAttributes = [
            .foregroundColor: backgroundColor
        ]

        UITabBar.appearance().standardAppearance = appearance
        UITabBar.appearance().scrollEdgeAppearance = appearance
    }

    func setBlurryBackgroundForTabItemRow(){
        // Set up a custom tab bar appearance with system material
        let tabBarAppearance = UITabBarAppearance()

        // Set the background to a system material style
        tabBarAppearance.backgroundEffect = UIBlurEffect(style: .systemThickMaterial)

        // Apply the appearance globally
        UITabBar.appearance().standardAppearance = tabBarAppearance
        UITabBar.appearance().scrollEdgeAppearance = tabBarAppearance
    }
}
