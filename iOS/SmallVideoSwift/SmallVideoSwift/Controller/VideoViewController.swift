//
//  VideoViewController.swift
//  SmallVideoSwift
//
//  Created by yupeng xia on 2020/10/26.
//

import Foundation
import UIKit

class VideoViewController: DPWebViewController {

    override func viewDidLoad() {
        super.viewDidLoad()
        
        webView.load(NSURLRequest(url: NSURL(string: "https:www.baidu.com")! as URL) as URLRequest)
    }
    
}
