#import "MapObjectVisitor.h"

@implementation MapObjectVisitor

- (void)onPolylineVisited:(nonnull YMKPolylineMapObject *)polyline {
    self.polyline = polyline;
}

- (void)onPolygonVisited:(nonnull YMKPolygonMapObject *)polygon {
    self.polygon = polygon;
}

- (void)onPlacemarkVisited:(nonnull YMKPlacemarkMapObject *)placemark {}
- (void)onCircleVisited:(nonnull YMKCircleMapObject *)circle {}
- (BOOL)onCollectionVisitStart:(nonnull YMKMapObjectCollection *)collection { return YES; }
- (void)onCollectionVisitEnd:(nonnull YMKMapObjectCollection *)collection {}
- (BOOL)onClusterizedCollectionVisitStart:(nonnull YMKClusterizedPlacemarkCollection *)collection { return YES; }
- (void)onClusterizedCollectionVisitEnd:(nonnull YMKClusterizedPlacemarkCollection *)collection {}

- (void)onCircleVisitedWithCircle:(nonnull YMKCircleMapObject *)circle { 
  
}

- (void)onClusterizedCollectionVisitEndWithCollection:(nonnull YMKClusterizedPlacemarkCollection *)collection { 
   
}

- (BOOL)onClusterizedCollectionVisitStartWithCollection:(nonnull YMKClusterizedPlacemarkCollection *)collection { 
    return YES;
}

- (void)onCollectionVisitEndWithCollection:(nonnull YMKMapObjectCollection *)collection { 
  
}

- (BOOL)onCollectionVisitStartWithCollection:(nonnull YMKMapObjectCollection *)collection { 
    return YES;
}

- (void)onPlacemarkVisitedWithPlacemark:(nonnull YMKPlacemarkMapObject *)placemark { 
 
}

- (void)onPolygonVisitedWithPolygon:(nonnull YMKPolygonMapObject *)polygon { 
    self.polygon = polygon;
}

- (void)onPolylineVisitedWithPolyline:(nonnull YMKPolylineMapObject *)polyline { 
    self.polyline = polyline;
}

@end
