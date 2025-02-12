#import <Foundation/Foundation.h>
#import <YandexMapsMobile/YMKMapObjectVisitor.h>

@interface MapObjectVisitor : NSObject <YMKMapObjectVisitor>

@property (nonatomic, strong, nullable) YMKPolylineMapObject *polyline;
@property (nonatomic, strong, nullable) YMKPolygonMapObject *polygon;

@end
