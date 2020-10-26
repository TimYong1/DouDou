//
//  MyTabBarController.swift
//  SmallVideoSwift
//
//  Created by yupeng xia on 2020/10/26.
//

import UIKit

class DPTabBarController: UITabBarController {
    
    override func viewDidLoad() {
        super.viewDidLoad()
//        let oneVC = HomeViewController()
//        setChildViewController(vc: oneVC, title: "首页", imageName: "toolbar_01", selectedImageName: "toolbar_01_s")
//        
//        let towVC = WebSiteViewController()
//        setChildViewController(vc: towVC, title: "资源设置", imageName: "toolbar_02", selectedImageName: "toolbar_02_s")
//        
//        let threeVC = ChatViewController()
//        setChildViewController(vc: threeVC, title: "交流圈", imageName: "toolbar_03", selectedImageName: "toolbar_03_s")
//        
//        let fourVC = UserCenterViewController()
//        setChildViewController(vc: fourVC, title: "我的", imageName: "toolbar_04", selectedImageName: "toolbar_04_s")
        
//        UITabBar.appearance().backgroundColor = UIColor(red:0/255, green:31/255, blue:52/255, alpha:1)
//        UITabBar.appearance().backgroundImage = UIImage()

    }
    
    //初始化主控制器
    func setChildViewController(vc: UIViewController, title: String, imageName: String, selectedImageName: String) {
        vc.tabBarItem.image = UIImage(named: imageName)
        vc.tabBarItem.selectedImage = UIImage(named: selectedImageName)
        vc.tabBarItem.title = title
        
        //添加导航控制为 TabController 的子控制器
        let navVc = DPNavigationController(rootViewController: vc)
        self.addChild(navVc)
    }
    
}
