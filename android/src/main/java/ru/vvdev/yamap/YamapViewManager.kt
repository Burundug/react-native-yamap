package ru.vvdev.yamap

import android.view.View
import com.facebook.infer.annotation.Assertions
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.WritableArray
import com.facebook.react.common.MapBuilder
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.ViewGroupManager
import com.facebook.react.uimanager.annotations.ReactProp
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Geo
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.geo.Projection
import com.yandex.mapkit.geometry.geo.XYPoint
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.CircleMapObject
import com.yandex.mapkit.map.ClusterizedPlacemarkCollection
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.map.MapObjectVisitor
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.map.PolygonMapObject
import com.yandex.mapkit.map.PolylineMapObject
import ru.vvdev.yamap.view.YamapView
import javax.annotation.Nonnull
import kotlin.math.round

class YamapViewManager internal constructor() : ViewGroupManager<YamapView>() {
    override fun getName(): String {
        return REACT_CLASS
    }

    override fun getExportedCustomDirectEventTypeConstants(): Map<String, Any>? {
        return MapBuilder.builder<String, Any>()
            .build()
    }

    override fun getExportedCustomBubblingEventTypeConstants(): Map<String, Any> {
        return MapBuilder.builder<String, Any>()
            .put(
                "routes",
                MapBuilder.of("phasedRegistrationNames", MapBuilder.of("bubbled", "onRouteFound"))
            )
            .put(
                "routeLength",
                MapBuilder.of(
                    "phasedRegistrationNames",
                    MapBuilder.of("bubbled", "onRouteLengthReceived")
                )
            )
            .put(
                "closestPoint",
                MapBuilder.of(
                    "phasedRegistrationNames",
                    MapBuilder.of("bubbled", "onClosestPointReceived")
                )
            )
            .put(
                "cameraPosition",
                MapBuilder.of(
                    "phasedRegistrationNames",
                    MapBuilder.of("bubbled", "onCameraPositionReceived")
                )
            )
            .put(
                "cameraPositionChange",
                MapBuilder.of(
                    "phasedRegistrationNames",
                    MapBuilder.of("bubbled", "onCameraPositionChange")
                )
            )
            .put(
                "cameraPositionChangeEnd",
                MapBuilder.of(
                    "phasedRegistrationNames",
                    MapBuilder.of("bubbled", "onCameraPositionChangeEnd")
                )
            )
            .put(
                "visibleRegion",
                MapBuilder.of(
                    "phasedRegistrationNames",
                    MapBuilder.of("bubbled", "onVisibleRegionReceived")
                )
            )
            .put(
                "onMapPress",
                MapBuilder.of("phasedRegistrationNames", MapBuilder.of("bubbled", "onMapPress"))
            )
            .put(
                "onMapLongPress",
                MapBuilder.of("phasedRegistrationNames", MapBuilder.of("bubbled", "onMapLongPress"))
            )
            .put(
                "onMapLoaded",
                MapBuilder.of("phasedRegistrationNames", MapBuilder.of("bubbled", "onMapLoaded"))
            )
            .put(
                "onPolylineAdd",
                MapBuilder.of("phasedRegistrationNames", MapBuilder.of("bubbled", "onPolylineAdd"))
            )
            .put(
                "screenToWorldPoints",
                MapBuilder.of(
                    "phasedRegistrationNames",
                    MapBuilder.of("bubbled", "onScreenToWorldPointsReceived")
                )
            )
            .put(
                "worldToScreenPoints",
                MapBuilder.of(
                    "phasedRegistrationNames",
                    MapBuilder.of("bubbled", "onWorldToScreenPointsReceived")
                )
            )
            .build()
    }

    override fun getCommandsMap(): Map<String, Int>? {
        val map: MutableMap<String, Int> = MapBuilder.newHashMap()
        map["setCenter"] = SET_CENTER
        map["fitAllMarkers"] = FIT_ALL_MARKERS
        map["findRoutes"] = FIND_ROUTES
        map["setZoom"] = SET_ZOOM
        map["getCameraPosition"] = GET_CAMERA_POSITION
        map["getVisibleRegion"] = GET_VISIBLE_REGION
        map["setTrafficVisible"] = SET_TRAFFIC_VISIBLE
        map["fitMarkers"] = FIT_MARKERS
        map["getScreenPoints"] = GET_SCREEN_POINTS
        map["getWorldPoints"] = GET_WORLD_POINTS

        return map
    }

    override fun receiveCommand(
        view: YamapView,
        commandType: String,
        args: ReadableArray?
    ) {
        Assertions.assertNotNull(view)
        Assertions.assertNotNull(args)

        when (commandType) {
            "setCenter" -> setCenter(
                castToYaMapView(view),
                args!!.getMap(0),
                args.getDouble(1).toFloat(),
                args.getDouble(2).toFloat(),
                args.getDouble(3).toFloat(),
                args.getDouble(4).toFloat(),
                args.getInt(5)
            )

            "fitAllMarkers" -> fitAllMarkers(view)
            "fitMarkers" -> if (args != null) {
                fitMarkers(view, args.getArray(0))
            }

            "findRoutes" -> if (args != null) {
                args.getString(2)?.let { findRoutes(view, args.getArray(0), args.getArray(1), it) }
            }

            "setZoom" -> if (args != null) {
                view.setZoom(
                    args.getDouble(0).toFloat(),
                    args.getDouble(1).toFloat(),
                    args.getInt(2)
                )
            }

            "getPolygonCoords" -> if (args != null) {
                val map = view.mapWindow.map
                val mapObjects = map.mapObjects
                var polyline: PolylineMapObject? = null
                var polygon: PolygonMapObject? = null
                var projection = map.projection()

                mapObjects.traverse(object : MapObjectVisitor {

                    override fun onPolylineVisited(p0: PolylineMapObject) {
                        polyline = p0
                    }

                    override fun onPolygonVisited(p0: PolygonMapObject) {
                        polygon = p0
                    }

                    override fun onPlacemarkVisited(p0: PlacemarkMapObject) {}
                    override fun onCircleVisited(p0: CircleMapObject) {}
                    override fun onCollectionVisitStart(p0: MapObjectCollection): Boolean = true
                    override fun onCollectionVisitEnd(p0: MapObjectCollection) {}
                    override fun onClusterizedCollectionVisitStart(p0: ClusterizedPlacemarkCollection): Boolean =
                        true

                    override fun onClusterizedCollectionVisitEnd(p0: ClusterizedPlacemarkCollection) {}
                })


                if (polyline != null && polygon != null) {

                    val mainView = castToYaMapView(view)
                    val zoom = mainView.map.cameraPosition.zoom
                    var distanceOutside = analyzePolylineAndPolygon(
                        polyline!!.geometry.points,
                        polygon!!.geometry.outerRing.points,
                        projection,
                        zoom.toInt()
                    )
                    distanceOutside -= 500
                    val total = if (distanceOutside > 0) round(distanceOutside / 1000) else 0
                    view.emitRouteLength(total, args.getString(0))
                }
            }

            "findClosestPoint" -> if(args !== null) {
                val targetPoint: Point
                val p = args.getArray(0)
                targetPoint = Point(p!!.getDouble(0), p.getDouble(1))
                val pointsArray: WritableArray =  Arguments.createArray()
                for (i in 0 until args.getArray(1)!!.size()) {
                    val p = args.getArray(1)?.getArray(i)
                    pointsArray.pushMap(Arguments.createMap().apply {
                        if (p != null) {
                            putDouble("lat", p.getDouble(0))
                        }
                        if (p != null) {
                            putDouble("lon", p.getDouble(1))
                        }
                    })
                }

                val pointsList: List<Point> = (0 until pointsArray.size()).map { i ->
                    val map = pointsArray.getMap(i)
                    map?.let { Point(it.getDouble("lat"), map.getDouble("lon")) }!!
                }
                val closest = findClosestPoint(targetPoint, pointsList)
                view.emitClosestPoint(closest!!, args.getString(2))
            }

            "getCameraPosition" -> if (args != null) {
                view.emitCameraPositionToJS(args.getString(0))
            }

            "getVisibleRegion" -> if (args != null) {
                view.emitVisibleRegionToJS(args.getString(0))
            }

            "setTrafficVisible" -> if (args != null) {
                view.setTrafficVisible(args.getBoolean(0))
            }

            "getScreenPoints" -> if (args != null) {
                args.getArray(0)?.let { view.emitWorldToScreenPoints(it, args.getString(1)) }
            }

            "getWorldPoints" -> if (args != null) {
                args.getArray(0)?.let { view.emitScreenToWorldPoints(it, args.getString(1)) }
            }

            else -> throw IllegalArgumentException(
                String.format(
                    "Unsupported command %d received by %s.",
                    commandType,
                    javaClass.simpleName
                )
            )
        }
    }

    private fun isPointInsidePixelPolygon(point: XYPoint, polygon: List<XYPoint>): Boolean {
        val x = point.x
        val y = point.y
        var inside = false
        var j = polygon.size - 1

        for (i in polygon.indices) {
            val xi = polygon[i].x
            val yi = polygon[i].y
            val xj = polygon[j].x
            val yj = polygon[j].y

            val intersect = ((yi > y) != (yj > y)) &&
                    (x < (xj - xi) * (y - yi) / (yj - yi) + xi)

            if (intersect) inside = !inside
            j = i
        }
        return inside
    }

    private fun doesPolylineIntersectPolygon(
        polyline: List<XYPoint>,
        polygon: List<XYPoint>
    ): Boolean {
        for (i in 0 until polyline.size - 1) {
            val segmentStart = polyline[i]
            val segmentEnd = polyline[i + 1]

            for (j in 0 until polygon.size - 1) {
                val polygonStart = polygon[j]
                val polygonEnd = polygon[j + 1]

                if (doSegmentsIntersect(segmentStart, segmentEnd, polygonStart, polygonEnd)) {
                    return true
                }
            }
        }
        return false
    }

    private fun countPointsOnPolygonBoundary(polyline: List<XYPoint>, polygon: List<XYPoint>): Int {
        var count = 0

        for (point in polyline) {
            for (j in 0 until polygon.size - 1) {
                val polygonStart = polygon[j]
                val polygonEnd = polygon[j + 1]

                if (isPointOnSegmentXY(point, polygonStart, polygonEnd)) {
                    count++
                    break
                }
            }
        }

        return count
    }

    private fun isPointOnSegmentXY(p: XYPoint, a: XYPoint, b: XYPoint): Boolean {
        val crossProduct = (p.y - a.y) * (b.x - a.x) - (p.x - a.x) * (b.y - a.y)
        if (crossProduct != 0.0) return false

        val dotProduct = (p.x - a.x) * (b.x - a.x) + (p.y - a.y) * (b.y - a.y)
        if (dotProduct < 0) return false

        val squaredLength = (b.x - a.x) * (b.x - a.x) + (b.y - a.y) * (b.y - a.y)
        return dotProduct <= squaredLength
    }

    private fun doSegmentsIntersect(a: XYPoint, b: XYPoint, c: XYPoint, d: XYPoint): Boolean {
        fun crossProduct(p1: XYPoint, p2: XYPoint, p3: XYPoint): Double {
            return (p2.x - p1.x) * (p3.y - p1.y) - (p2.y - p1.y) * (p3.x - p1.x)
        }

        val d1 = crossProduct(c, d, a)
        val d2 = crossProduct(c, d, b)
        val d3 = crossProduct(a, b, c)
        val d4 = crossProduct(a, b, d)

        if (((d1 > 0 && d2 < 0) || (d1 < 0 && d2 > 0)) && ((d3 > 0 && d4 < 0) || (d3 < 0 && d4 > 0))) {
            return true
        }

        return false
    }

    private fun getSegmentIntersection(
        p1: XYPoint, p2: XYPoint, p3: XYPoint, p4: XYPoint
    ): XYPoint? {
        val denominator = (p4.y - p3.y) * (p2.x - p1.x) - (p4.x - p3.x) * (p2.y - p1.y)
        if (denominator == 0.0) return null

        val ua = ((p4.x - p3.x) * (p1.y - p3.y) - (p4.y - p3.y) * (p1.x - p3.x)) / denominator
        val ub = ((p2.x - p1.x) * (p1.y - p3.y) - (p2.y - p1.y) * (p1.x - p3.x)) / denominator

        if (ua in 0.0..1.0 && ub in 0.0..1.0) {
            val x = p1.x + ua * (p2.x - p1.x)
            val y = p1.y + ua * (p2.y - p1.y)
            return XYPoint(x, y)
        }
        return null
    }

    private fun findIntersectionWithPolygonByPixels(
        start: XYPoint,
        end: XYPoint,
        polygon: List<XYPoint>
    ): XYPoint? {
        for (j in 0 until polygon.size - 1) {
            val polygonStart = polygon[j]
            val polygonEnd = polygon[j + 1]

            val intersection = getSegmentIntersection(start, end, polygonStart, polygonEnd)
            if (intersection != null) {
                return intersection
            }
        }
        return null
    }

    fun analyzePolylineAndPolygon(
        polyline: List<Point>,
        polygon: List<Point>,
        projection: Projection,
        zoom: Int
    ): Double {
        val polylinePixels = getXYFromCoords(polyline, projection, zoom)
        val polygonPixels = getXYFromCoords(polygon, projection, zoom)

        val pointsInside = mutableListOf<XYPoint>()
        val pointsOutside = mutableListOf<XYPoint>()
        val boundaryPoints = mutableListOf<XYPoint>()

        for (i in polylinePixels.indices) {
            val point = polylinePixels[i]
            val isInside = isPointInsidePixelPolygon(point, polygonPixels)
            var isOnBoundary = false


            for (j in 0 until polygonPixels.size - 1) {
                val polygonStart = polygonPixels[j]
                val polygonEnd = polygonPixels[j + 1]

                if (doSegmentsIntersect(point, point, polygonStart, polygonEnd)) {
                    boundaryPoints.add(point)
                    isOnBoundary = true
                    break
                }
            }

            if (isInside) {
                pointsInside.add(point)
            } else if (!isOnBoundary) {
                if (i > 0) {
                    val prevPoint = polylinePixels[i - 1]
                    val wasInside = isPointInsidePixelPolygon(prevPoint, polygonPixels)


                    if (wasInside) {
                        val intersection =
                            findIntersectionWithPolygonByPixels(prevPoint, point, polygonPixels)
                        if (intersection != null) {
                            pointsInside.add(intersection)
                            pointsOutside.add(intersection)
                        }
                    }
                }
                pointsOutside.add(point)
            }
        }

        val borderCount = countPointsOnPolygonBoundary(polylinePixels, polygonPixels)
        val intersects = doesPolylineIntersectPolygon(polylinePixels, polygonPixels)
        val coordsOutside = getCoordsFromXY(pointsOutside, projection, zoom)
        val coordsInside = getCoordsFromXY(pointsInside, projection, zoom)
        val distOutside = calculateOutsideDistance(coordsOutside)
        val distInside = calculateOutsideDistance(coordsInside)
        println("Total points: ${polylinePixels.size}")
        println("Points inside: ${pointsInside.size}")
        println("Points outside: ${pointsOutside.size}")
        println("has intersects? $intersects")
        println("Total points outside distance:$distOutside")
        println("Total points inside distance: $distInside")
        println("Total border points: $borderCount")
        return distOutside
    }

    private fun getCoordsFromXY(
        points: List<XYPoint>,
        projection: Projection,
        zoom: Int
    ): List<Point> {
        return points.map { point ->
            projection.xyToWorld(point, zoom)
        }
    }


    private fun findClosestPoint(targetPoint: Point, points: List<Point>): Point? {
        return points.minByOrNull { Geo.distance(targetPoint, it) }
    }

    private fun getXYFromCoords(
        points: List<Point>,
        projection: Projection,
        zoom: Int
    ): List<XYPoint> {
        return points.map { point ->
            projection.worldToXY(point, zoom)
        }
    }


    fun calculateOutsideDistance(pointsOutside: List<Point>): Double {
        if (pointsOutside.size < 2) return 0.0

        var totalDistance = 0.0
        for (i in 0 until pointsOutside.size - 1) {
            val start = pointsOutside[i]
            val end = pointsOutside[i + 1]

            totalDistance += Geo.distance(start, end)
        }
        return totalDistance
    }

    private fun castToYaMapView(view: View): YamapView {
        return view as YamapView
    }

    @Nonnull
    public override fun createViewInstance(@Nonnull context: ThemedReactContext): YamapView {
        val view = YamapView(context)
        MapKitFactory.getInstance().onStart()
        view.onStart()

        return view
    }

    private fun setCenter(
        view: YamapView,
        center: ReadableMap?,
        zoom: Float,
        azimuth: Float,
        tilt: Float,
        duration: Float,
        animation: Int
    ) {
        if (center != null) {
            val centerPosition = Point(center.getDouble("lat"), center.getDouble("lon"))
            val pos = CameraPosition(centerPosition, zoom, azimuth, tilt)
            view.setCenter(pos, duration, animation)
        }
    }

    private fun fitAllMarkers(view: View) {
        castToYaMapView(view).fitAllMarkers()
    }

    private fun fitMarkers(view: View, jsPoints: ReadableArray?) {
        if (jsPoints != null) {
            val points = ArrayList<Point?>()

            for (i in 0 until jsPoints.size()) {
                val point = jsPoints.getMap(i)
                if (point != null) {
                    points.add(Point(point.getDouble("lat"), point.getDouble("lon")))
                }
            }

            castToYaMapView(view).fitMarkers(points)
        }
    }

    private fun findRoutes(
        view: View,
        jsPoints: ReadableArray?,
        jsVehicles: ReadableArray?,
        id: String
    ) {
        if (jsPoints != null) {
            val points = ArrayList<Point?>()

            for (i in 0 until jsPoints.size()) {
                val point = jsPoints.getMap(i)
                if (point != null) {
                    points.add(Point(point.getDouble("lat"), point.getDouble("lon")))
                }
            }

            val vehicles = ArrayList<String>()

            if (jsVehicles != null) {
                for (i in 0 until jsVehicles.size()) {
                    jsVehicles.getString(i)?.let { vehicles.add(it) }
                }
            }

            castToYaMapView(view).findRoutes(points, vehicles, id)
        }
    }

    // PROPS
    @ReactProp(name = "userLocationIcon")
    fun setUserLocationIcon(view: View, icon: String?) {
        if (icon != null) {
            castToYaMapView(view).setUserLocationIcon(icon)
        }
    }

    @ReactProp(name = "userLocationIconScale")
    fun setUserLocationIconScale(view: View, scale: Float) {
        castToYaMapView(view).setUserLocationIconScale(scale)
    }

    @ReactProp(name = "userLocationAccuracyFillColor")
    fun setUserLocationAccuracyFillColor(view: View, color: Int) {
        castToYaMapView(view).setUserLocationAccuracyFillColor(color)
    }

    @ReactProp(name = "userLocationAccuracyStrokeColor")
    fun setUserLocationAccuracyStrokeColor(view: View, color: Int) {
        castToYaMapView(view).setUserLocationAccuracyStrokeColor(color)
    }

    @ReactProp(name = "userLocationAccuracyStrokeWidth")
    fun setUserLocationAccuracyStrokeWidth(view: View, width: Float) {
        castToYaMapView(view).setUserLocationAccuracyStrokeWidth(width)
    }

    @ReactProp(name = "showUserPosition")
    fun setShowUserPosition(view: View, show: Boolean?) {
        castToYaMapView(view).setShowUserPosition(show!!)
    }

    @ReactProp(name = "nightMode")
    fun setNightMode(view: View, nightMode: Boolean?) {
        castToYaMapView(view).setNightMode(nightMode ?: false)
    }

    @ReactProp(name = "scrollGesturesEnabled")
    fun setScrollGesturesEnabled(view: View, scrollGesturesEnabled: Boolean) {
        castToYaMapView(view).setScrollGesturesEnabled(scrollGesturesEnabled == true)
    }

    @ReactProp(name = "rotateGesturesEnabled")
    fun setRotateGesturesEnabled(view: View, rotateGesturesEnabled: Boolean) {
        castToYaMapView(view).setRotateGesturesEnabled(rotateGesturesEnabled == true)
    }

    @ReactProp(name = "zoomGesturesEnabled")
    fun setZoomGesturesEnabled(view: View, zoomGesturesEnabled: Boolean) {
        castToYaMapView(view).setZoomGesturesEnabled(zoomGesturesEnabled == true)
    }

    @ReactProp(name = "tiltGesturesEnabled")
    fun setTiltGesturesEnabled(view: View, tiltGesturesEnabled: Boolean) {
        castToYaMapView(view).setTiltGesturesEnabled(tiltGesturesEnabled == true)
    }

    @ReactProp(name = "fastTapEnabled")
    fun setFastTapEnabled(view: View, fastTapEnabled: Boolean) {
        castToYaMapView(view).setFastTapEnabled(fastTapEnabled == true)
    }

    @ReactProp(name = "mapStyle")
    fun setMapStyle(view: View, style: String?) {
        if (style != null) {
            castToYaMapView(view).setMapStyle(style)
        }
    }

    @ReactProp(name = "mapType")
    fun setMapType(view: View, type: String?) {
        if (type != null) {
            castToYaMapView(view).setMapType(type)
        }
    }

    @ReactProp(name = "initialRegion")
    fun setInitialRegion(view: View, params: ReadableMap?) {
        if (params != null) {
            castToYaMapView(view).setInitialRegion(params)
        }
    }

    @ReactProp(name = "maxFps")
    fun setMaxFps(view: View, maxFps: Float) {
        castToYaMapView(view).setMaxFps(maxFps)
    }

    @ReactProp(name = "interactive")
    fun setInteractive(view: View, interactive: Boolean) {
        castToYaMapView(view).setInteractive(interactive)
    }

    @ReactProp(name = "logoPosition")
    fun setLogoPosition(view: View, params: ReadableMap?) {
        if (params != null) {
            castToYaMapView(view).setLogoPosition(params)
        }
    }

    @ReactProp(name = "logoPadding")
    fun setLogoPadding(view: View, params: ReadableMap?) {
        if (params != null) {
            castToYaMapView(view).setLogoPadding(params)
        }
    }

    override fun addView(parent: YamapView, child: View, index: Int) {
        parent.addFeature(child, index)
        super.addView(parent, child, index)
    }

    override fun removeViewAt(parent: YamapView, index: Int) {
        parent.removeChild(index)
        super.removeViewAt(parent, index)
    }

    companion object {
        const val REACT_CLASS: String = "YamapView"

        private const val SET_CENTER = 1
        private const val FIT_ALL_MARKERS = 2
        private const val FIND_ROUTES = 3
        private const val SET_ZOOM = 4
        private const val GET_CAMERA_POSITION = 5
        private const val GET_VISIBLE_REGION = 6
        private const val SET_TRAFFIC_VISIBLE = 7
        private const val FIT_MARKERS = 8
        private const val GET_SCREEN_POINTS = 9
        private const val GET_WORLD_POINTS = 10
    }
}
