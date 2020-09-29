//
//  ZhcwViewController.m
//  DPPost
//
//  Created by xiayupeng on 2019/12/17.
//  Copyright © 2019 xiayupeng. All rights reserved.
//

#import "DPPost.h"
#import <CommonCrypto/CommonDigest.h>
#import "SBJson5.h"
#import "AFNetworking.h"
#import "UserInfo.h"

@implementation DPPost
+ (void)getRongCloudTokenWhithUserId:(NSString *_Nullable)userId userName:(NSString *_Nullable)aUserName portraitUri:(NSString *_Nonnull)aPortraitUri successBlock:(void(^_Nullable)(NSDictionary * __nullable obj))aSuccessBlock failedBlock:(void(^_Nullable)(NSString * __nullable obj))aFailedBlock{
    
    NSString *nonce = [NSString stringWithFormat:@"%d",arc4random()];
    NSDate *dateObc = [NSDate date];
    NSString *timestamp = [NSString stringWithFormat:@"%d",(int)[dateObc timeIntervalSince1970]];
    NSString *signature = [DPPost sha1:[NSString stringWithFormat:@"%@%@%@",[UserInfo shareInstance].appSecret,nonce,timestamp]];
    
    NSDictionary *dic = @{@"userId" : userId,
                          @"name" : aUserName,
                          @"portraitUri" : aPortraitUri};
    
    AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
    manager.responseSerializer = [[AFHTTPResponseSerializer alloc]init];
    [manager.requestSerializer setValue:[UserInfo shareInstance].appKey forHTTPHeaderField:@"App-Key"];
    [manager.requestSerializer setValue:nonce forHTTPHeaderField:@"Nonce"];
    [manager.requestSerializer setValue:timestamp forHTTPHeaderField:@"Timestamp"];
    [manager.requestSerializer setValue:signature forHTTPHeaderField:@"Signature"];
    [manager.requestSerializer setValue:@"application/x-www-form-urlencoded" forHTTPHeaderField:@"Content-Type"];
    
    [manager POST:[UserInfo shareInstance].tokenUrl parameters:dic progress:nil success:^(NSURLSessionDataTask * _Nonnull task, id _Nullable responseObject) {
        NSString *responseStr = [[NSString alloc] initWithData:responseObject encoding:NSUTF8StringEncoding];
        NSDictionary *jsonDic = [NSJSONSerialization JSONObjectWithData:[responseStr dataUsingEncoding:NSUTF8StringEncoding] options:NSJSONReadingMutableContainers error:nil];
        aSuccessBlock(jsonDic);
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        aFailedBlock(@"网络请求失败!");
    }];
}

+ (NSString *)sha1:(NSString *)input {
    const char *cstr = [input cStringUsingEncoding:NSUTF8StringEncoding];
    NSData *data = [NSData dataWithBytes:cstr length:input.length];
    
    uint8_t digest[CC_SHA1_DIGEST_LENGTH];
    CC_SHA1(data.bytes, (unsigned int)data.length, digest);
    NSMutableString *output = [NSMutableString stringWithCapacity:CC_SHA1_DIGEST_LENGTH * 2];
    
    for(int i=0; i<CC_SHA1_DIGEST_LENGTH; i++) {
        [output appendFormat:@"%02x", digest[i]];
    }
    return output;
}
@end
