#import <React/RCTConvert.h>
#import <Foundation/Foundation.h>
@import YandexMapsMobile;

@interface RCTConvert(Yamap)

@end

@implementation RCTConvert(Yamap)

+ (YMKPoint*)YMKPoint:(id)json {
    json = [self NSDictionary:json];
    YMKPoint *target = [YMKPoint pointWithLatitude:[self double:json[@"lat"]] longitude:[self double:json[@"lon"]]];

    return target;
}

+ (YMKScreenPoint*)YMKScreenPoint:(id)json {
    json = [self NSDictionary:json];
    YMKScreenPoint *target = [YMKScreenPoint screenPointWithX:[self float:json[@"x"]] y:[self float:json[@"y"]]];

    return target;
}

+ (NSArray*)Vehicles:(id)json {
    return [self NSArray:json];
}

+ (NSMutableArray<YMKPoint*>*)Points:(id)json {
    NSArray* parsedArray = [self NSArray:json];
    NSMutableArray* result = [[NSMutableArray alloc] init];

    for (NSDictionary* jsonMarker in parsedArray) {
        double lat = [[jsonMarker valueForKey:@"lat"] doubleValue];
        double lon = [[jsonMarker valueForKey:@"lon"] doubleValue];
        YMKPoint *point = [YMKPoint pointWithLatitude:lat longitude:lon];
        [result addObject:point];
    }

    return result;
}

+ (NSMutableArray<YMKPoint*>*)PointsArray:(id)json {
    NSArray *parsedArray = [self NSArray:json]; // Конвертируем в NSArray
    NSMutableArray *result = [[NSMutableArray alloc] init];

    for (NSArray *pointArray in parsedArray) {
        if (![pointArray isKindOfClass:[NSArray class]] || pointArray.count < 2) {
            NSLog(@"Skipping invalid point: %@", pointArray);
            continue;
        }

        YMKPoint *point =  [self PointFromArray: pointArray];
        [result addObject:point];
    }

    return result;
}

+ (YMKPoint*)PointFromArray:(id)json {

    if (![json isKindOfClass:[NSArray class]] || [json count] < 2) {
        NSLog(@"Error: json is not a valid array [lat, lon]");
    }

    double lat = [json[0] doubleValue];
    double lon = [json[1] doubleValue];

    return [YMKPoint pointWithLatitude:lat longitude:lon];
}

+ (NSMutableArray<YMKScreenPoint*>*)ScreenPoints:(id)json {
    NSArray* parsedArray = [self NSArray:json];
    NSMutableArray* result = [[NSMutableArray alloc] init];

    for (NSDictionary* jsonMarker in parsedArray) {
        float x = [[jsonMarker valueForKey:@"x"] floatValue];
        float y = [[jsonMarker valueForKey:@"y"] floatValue];
        YMKScreenPoint *point = [YMKScreenPoint screenPointWithX:x y:y];
        [result addObject:point];
    }

    return result;
}

+ (float)Zoom:(id)json {
    json = [self NSDictionary:json];
    return [self float:json[@"zoom"]];
}

+ (float)Azimuth:(id)json {
    json = [self NSDictionary:json];
    return [self float:json[@"azimuth"]];
}

+ (float)Tilt:(id)json {
    json = [self NSDictionary:json];
    return [self float:json[@"tilt"]];
}

@end
