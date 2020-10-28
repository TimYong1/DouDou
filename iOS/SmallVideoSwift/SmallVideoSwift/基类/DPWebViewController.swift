//
//  DPWebViewController.swift
//  SmallVideoSwift
//
//  Created by yupeng xia on 2020/10/29.
//

import Foundation
import UIKit
import WebKit

class DPWebViewController: DPBaseViewController, WKNavigationDelegate {
    
    var webView:WKWebView!
    
    override func loadView() {
        let webConfiguration = WKWebViewConfiguration();
        webView = WKWebView(frame: .zero, configuration: webConfiguration)
        webView.navigationDelegate = self;
        view = webView;
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
    }
    
    func webView(_ webView: WKWebView, didFinish navigation: WKNavigation!) {
        webView.evaluateJavaScript("displayDate()") { (any, error) in
            if (error != nil) {
                print(error ?? "err")
            }
        }
    }
 
}
