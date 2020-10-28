//
//  Normal.swift
//  SmallVideoSwift
//
//  Created by yupeng xia on 2020/10/28.
//

import Foundation
import UIKit

var isFullScreen: Bool {
    if #available(iOS 11, *) {
        guard let w = UIApplication.shared.delegate?.window, let unwrapedWindow = w else {
            return false
        }
        
        if unwrapedWindow.safeAreaInsets.left > 0 || unwrapedWindow.safeAreaInsets.bottom > 0 {
            return true
        }
    }
    return false
}

var dNavigationBarHeight: CGFloat {
    return isFullScreen ? 88 : 64
}

var dBottomSafeHeight: CGFloat {
    return isFullScreen ? 34 : 0
}
