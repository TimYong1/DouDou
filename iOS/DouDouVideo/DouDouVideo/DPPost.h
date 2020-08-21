//
//  ZhcwViewController.h
//  ZhcwViewController
//
//  Created by xiayupeng on 2019/12/17.
//  Copyright © 2019 xiayupeng. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <Foundation/Foundation.h>

@interface DPPost : NSObject
+ (void)getRongCloudTokenWhithUserId:(NSString *_Nullable)userId userName:(NSString *_Nullable)aUserName portraitUri:(NSString *_Nonnull)aPortraitUri successBlock:(void(^_Nullable)(NSDictionary * __nullable obj))aSuccessBlock failedBlock:(void(^_Nullable)(NSString * __nullable obj))aFailedBlock;
@end

