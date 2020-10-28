//
//  DPTabBarController.swift
//  SmallVideoSwift
//
//  Created by yupeng xia on 2020/10/26.
//

import UIKit

class DPTabBarController: UITabBarController {
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        var tabbarStyleDic: Dictionary<String,Any?>? = nil
        if (tabbarStyleDic == nil) {
            var tabBarItemAry = [[String: Any]] ()
            tabBarItemAry = [
                ["dpTabTitle":"看电影",
                 "dpTab":"dpTabOne",
                 "dpTabbarH":"dpTabOneH",
                 "dpTabType":"video"],
                
                ["dpTabTitle":"聊天",
                 "dpTab":"dpTabTow",
                 "dpTabbarH":"dpTabTowH",
                 "dpTabType":"webSite"],
                
                ["dpTabTitle":"朋友圈",
                 "dpTab":"dpTabThree",
                 "dpTabbarH":"dpTabThreeH",
                 "dpTabType":"chat"],
                
                ["dpTabTitle":"我的",
                 "dpTab":"dpTabFour",
                 "dpTabbarH":"dpTabFourH",
                 "dpTabType":"user"]
            ]
            
            tabbarStyleDic = ["tabBGColor":"ffffff",
                              "dpTabTitleColor":"000000",
                              "dpTabTitleColorH":"ce2a2e",
                              "tabBarItemAry":tabBarItemAry]
        }
        
        let tabBGColor = tabbarStyleDic!["tabBGColor"]
        UITabBar.appearance().backgroundImage = UIImage()
        UITabBar.appearance().backgroundColor = UIColor.hexColorWithAlpha(color: (tabBGColor as! NSString) as String, alpha: 1)
        
        let dpTabTitleColor: UIColor = UIColor.hexColorWithAlpha(color: tabbarStyleDic?["dpTabTitleColor"] as! String, alpha: 1)
        let dpTabTitleColorH: UIColor = UIColor.hexColorWithAlpha(color: tabbarStyleDic?["dpTabTitleColorH"] as! String, alpha: 1)
        
        let tabBarItemAry = tabbarStyleDic!["tabBarItemAry"]
        for dic in (tabBarItemAry as! NSArray) {
            let tabBarItemDic: Dictionary<String,Any?>? = dic as? Dictionary<String, Any?>
            let dpTabTitle: String = tabBarItemDic?["dpTabTitle"] as! String
            let dpTab = tabBarItemDic?["dpTab"] as! String
            let dpTabbarH = tabBarItemDic?["dpTabbarH"] as! String
            let dpTabType = tabBarItemDic?["dpTabType"] as! String
            
            var vc: DPBaseViewController? = nil
            if dpTabType == "video" {
                vc = VideoViewController()
            }else if dpTabType == "webSite" {
                vc = WebSiteViewController()
            }else if dpTabType == "chat" {
                vc = ChatViewController()
            }else if dpTabType == "user" {
                vc = UserCenterViewController()
            }
            
            if vc != nil {
                setChildViewController(vc: vc!, title: dpTabTitle, titleColor: dpTabTitleColor, titleColorH: dpTabTitleColorH, imageName: dpTab, selectedImageName: dpTabbarH)
            }
        }
    }
    
    //初始化主控制器
    func setChildViewController(vc: DPBaseViewController, title: String, titleColor: UIColor, titleColorH: UIColor, imageName: String, selectedImageName: String) {
        vc.tabBarItem = UITabBarItem(title: title, image: UIImage(named: imageName), selectedImage: UIImage(named: selectedImageName))
        vc.tabBarItem.image = vc.tabBarItem.image!.withRenderingMode(.alwaysOriginal)
        vc.tabBarItem.selectedImage = vc.tabBarItem.selectedImage!.withRenderingMode(.alwaysOriginal)
        if #available(iOS 13.0, *) {
            tabBar.tintColor = UIColor.black
            tabBar.unselectedItemTintColor = UIColor.black
        }
        
        var fontValue:UIFont = UIFont.dpFont(ofSize: 14)
        if isFullScreen {
            fontValue = UIFont.dpFont(ofSize: 22)
            
            vc.tabBarItem.imageInsets = UIEdgeInsets(top: 8, left: 0, bottom: -8, right: 0)
            vc.tabBarItem.titlePositionAdjustment = UIOffset(horizontal: 0, vertical: 12)
        }
        vc.tabBarItem.setTitleTextAttributes([NSAttributedString.Key.foregroundColor: titleColor, NSAttributedString.Key.font: fontValue], for: .normal)
        vc.tabBarItem.setTitleTextAttributes([NSAttributedString.Key.foregroundColor: titleColorH, NSAttributedString.Key.font: fontValue], for: .selected)
        
        let navVc = DPNavigationController(rootViewController: vc)
        self.addChild(navVc)
    }
    
}
