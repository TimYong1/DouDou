//
//  UIFont+DPExtension.swift
//  SmallVideoSwift
//
//  Created by yupeng xia on 2020/10/28.
//

import Foundation
import UIKit

public extension UIFont {
    
    //默认字体适配
    class func dpFont(ofSize fontSize: CGFloat) -> UIFont
    {
        let fontSizeValue = CGFloat(min(UIScreen.main.bounds.height,UIScreen.main.bounds.width)) / 375.00 * fontSize;
        return UIFont.systemFont(ofSize: fontSizeValue)
    }
    
    //平方字体适配
    class func dpPFFont(ofSize fontSize: CGFloat) -> UIFont
    {
        let fontSizeValue = CGFloat(min(UIScreen.main.bounds.height,UIScreen.main.bounds.width)) / 375.00 * fontSize;
        return UIFont.init(name: "PingFang SC", size: fontSizeValue) ?? UIFont.systemFont(ofSize: fontSizeValue)
    }
    
}
