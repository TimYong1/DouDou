//
//  ViewController.m
//  DouDouVideo
//
//  Created by yupeng xia on 2020/8/19.
//  Copyright © 2020 yupeng xia. All rights reserved.
//

#import "ViewController.h"
#import <RongCallKit/RongCallKit.h>
#import <RongCallLib/RongCallLib.h>
#import <RongIMLib/RongIMLib.h>
#import <RongIMKit/RongIMKit.h>
#import "DPPost.h"
#import "UserInfo.h"

@interface ViewController (){
    
}
@property (nonatomic, strong) MBProgressHUD *hud;
@end

@implementation ViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    [[RCCall sharedRCCall] isAudioCallEnabled:ConversationType_PRIVATE];
    [[RCCall sharedRCCall] isVideoCallEnabled:ConversationType_PRIVATE];
    
    self.hud = [[MBProgressHUD alloc] initWithView:self.view];
    _hud.mode = MBProgressHUDBackgroundStyleSolidColor;
    _hud.backgroundColor = [UIColor colorWithRed:0 green:0 blue:0 alpha:0.3];
    _hud.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight;
    [self.view addSubview:_hud];
    
    NSString *randomId = [NSString stringWithFormat:@"%u",arc4random() % 10];
    [UserInfo shareInstance].name = randomId;
    [UserInfo shareInstance].userId = randomId;
    [UserInfo shareInstance].portraitUri = randomId;

    self.callIdLab.text = [NSString stringWithFormat:@"我的呼叫ID:%@",[UserInfo shareInstance].userId];

    [self getToken];
}

- (void)getToken {
    [_hud showAnimated:YES];
    
    //获取融云token
    arc_block(self);
    [DPPost getRongCloudTokenWhithUserId:[UserInfo shareInstance].userId userName:[UserInfo shareInstance].name portraitUri:[UserInfo shareInstance].portraitUri successBlock:^(NSDictionary * _Nullable obj) {
        dispatch_async(dispatch_get_main_queue(), ^{
            [weak_self.hud hideAnimated:YES];
        });
        
        NSString *code = [obj objectForKey:@"code"];
        NSString *token = [obj objectForKey:@"token"];
        if ([code integerValue] == 200) {
            [weak_self rongConnect:token];
        }else {
            [weak_self alertViewShow:[NSString stringWithFormat:@"获取token成功，code:%@",code]];
        }
    } failedBlock:^(NSString * _Nullable obj) {
        dispatch_async(dispatch_get_main_queue(), ^{
            [weak_self.hud hideAnimated:YES];
        });
        [weak_self alertViewShow:[NSString stringWithFormat:@"获取token失败，obj:%@",obj]];
    }];
}
- (void)rongConnect:(NSString *)cacheToken {
    [_hud showAnimated:YES];
    
    //登录融云服务器
    arc_block(self);
    [[RCIM sharedRCIM] connectWithToken:cacheToken success:^(NSString *userId) {
        dispatch_async(dispatch_get_main_queue(), ^{
            [weak_self.hud hideAnimated:YES];
            
            MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:self.view animated:YES];
            hud.mode = MBProgressHUDModeText;
            hud.label.text = @"登录成功!";
            hud.offset = CGPointMake(0.f, MBProgressMaxOffset);
            [hud hideAnimated:YES afterDelay:3.f];
        });
    } error:^(RCConnectErrorCode status) {
        dispatch_async(dispatch_get_main_queue(), ^{
            [weak_self.hud hideAnimated:YES];
        });
        [weak_self alertViewShow:[NSString stringWithFormat:@"登陆的错误码为:%ld", (long)status]];
    } tokenIncorrect:^{
        dispatch_async(dispatch_get_main_queue(), ^{
            [weak_self.hud hideAnimated:YES];
        });
        [weak_self alertViewShow:@"token错误"];
    }];
}

- (IBAction)callAction:(UIButton *)sender {
    if (_callIdTextField.text.length < 1) {
        [self alertViewShow:@"请输入呼叫ID!"];
        return;
    }
    [[RCCall sharedRCCall] startSingleCall:_callIdTextField.text mediaType:RCCallMediaVideo];
}

- (void)alertViewShow:(NSString *)message{
    UIAlertController *actionSheet = [UIAlertController alertControllerWithTitle:@"提示" message:message preferredStyle:UIAlertControllerStyleActionSheet];
    UIAlertAction *alert = [UIAlertAction actionWithTitle:@"知道了" style:UIAlertActionStyleDefault handler:nil];
    [actionSheet addAction:alert];
    [self presentViewController:actionSheet animated:YES completion:nil];
}
@end
