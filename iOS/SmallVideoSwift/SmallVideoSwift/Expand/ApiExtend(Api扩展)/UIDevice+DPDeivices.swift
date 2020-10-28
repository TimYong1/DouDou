//
//  UIDevice+DPDeivices.swift
//  SmallVideoSwift
//
//  Created by yupeng xia on 2020/10/28.
//

import Foundation
import UIKit
public extension UIDevice {
    //刘海屏， 获取底部高度
    class func getBottomHeight() -> CGFloat {
        let tabHeight = UIApplication.shared.statusBarFrame.size.height > 20 ? 34.0 : 0.0
        return CGFloat(tabHeight)
    }
}
