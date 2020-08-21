//
//  UserInfo.m
//  DouDouVideo
//
//  Created by yupeng xia on 2020/8/19.
//  Copyright © 2020 yupeng xia. All rights reserved.
//

#import "UserInfo.h"

static UserInfo *_userInfo = nil;
@implementation UserInfo
+ (instancetype)shareInstance{
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        if (_userInfo == nil) {
            _userInfo = [[self alloc]init];
        }
    });
    return _userInfo;
}

@end
