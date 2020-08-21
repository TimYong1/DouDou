//
//  AppDelegate.m
//  DouDouVideo
//
//  Created by yupeng xia on 2020/8/19.
//  Copyright © 2020 yupeng xia. All rights reserved.
//

#import "AppDelegate.h"
#import <RongIMKit/RongIMKit.h>
#import "DPPost.h"
#import "UserInfo.h"

@interface AppDelegate ()

@end

@implementation AppDelegate

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    [UserInfo shareInstance].tokenUrl = @"https://api-bj.ronghub.com/user/getToken.json";
    [UserInfo shareInstance].appKey = @"3argexb63svke";
    [UserInfo shareInstance].appSecret = @"NgmYSXvHH4h";

    //初始化融云SDK
    [[RCIM sharedRCIM] initWithAppKey:[UserInfo shareInstance].appKey];
    return YES;
}
@end
