//
//  AppDelegate.swift
//  Hello
//
//  Created by pingchas on 2020/10/8.
//  Copyright © 2020 pingchas. All rights reserved.
//

import UIKit

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {
    var window: UIWindow?
    internal func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        window = UIWindow(frame: UIScreen.main.bounds)
        window?.rootViewController = DPTabBarController()
        window?.makeKeyAndVisible()
        return true
    }
}
