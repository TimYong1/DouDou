//
//  UserInfo.h
//  DouDouVideo
//
//  Created by yupeng xia on 2020/8/19.
//  Copyright © 2020 yupeng xia. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface UserInfo : NSObject
@property (nonatomic, strong) NSString *tokenUrl;
@property (nonatomic, strong) NSString *appKey;
@property (nonatomic, strong) NSString *appSecret;
@property (nonatomic, strong) NSString *name;
@property (nonatomic, strong) NSString *userId;
@property (nonatomic, strong) NSString *portraitUri;

+ (instancetype)shareInstance;
@end

NS_ASSUME_NONNULL_END
