import React from 'react';
import { ViewProps, ImageSourcePropType, NativeSyntheticEvent } from 'react-native';
import { Point, ScreenPoint, DrivingInfo, MasstransitInfo, RoutesFoundEvent, Vehicles, CameraPosition, RouteLength, VisibleRegion, InitialRegion, Animation, MapLoaded, YandexLogoPosition, YandexLogoPadding } from '../interfaces';
export interface YaMapProps extends ViewProps {
    userLocationIcon?: ImageSourcePropType;
    userLocationIconScale?: number;
    showUserPosition?: boolean;
    nightMode?: boolean;
    mapStyle?: string;
    onCameraPositionChange?: (event: NativeSyntheticEvent<CameraPosition>) => void;
    onCameraPositionChangeEnd?: (event: NativeSyntheticEvent<CameraPosition>) => void;
    onMapPress?: (event: NativeSyntheticEvent<Point>) => void;
    onMapLongPress?: (event: NativeSyntheticEvent<Point>) => void;
    onMapLoaded?: (event: NativeSyntheticEvent<MapLoaded>) => void;
    onPolylineAdd?: (event: NativeSyntheticEvent<null>) => void;
    userLocationAccuracyFillColor?: string;
    userLocationAccuracyStrokeColor?: string;
    userLocationAccuracyStrokeWidth?: number;
    scrollGesturesEnabled?: boolean;
    zoomGesturesEnabled?: boolean;
    tiltGesturesEnabled?: boolean;
    rotateGesturesEnabled?: boolean;
    fastTapEnabled?: boolean;
    initialRegion?: InitialRegion;
    maxFps?: number;
    followUser?: boolean;
    logoPosition?: YandexLogoPosition;
    logoPadding?: YandexLogoPadding;
}
export declare class YaMap extends React.Component<YaMapProps> {
    static defaultProps: {
        showUserPosition: boolean;
        clusterColor: string;
        maxFps: number;
    };
    map: React.RefObject<YaMapNativeComponent>;
    static ALL_MASSTRANSIT_VEHICLES: Vehicles[];
    static init(apiKey: string): Promise<void>;
    static setLocale(locale: string): Promise<void>;
    static getLocale(): Promise<string>;
    static resetLocale(): Promise<void>;
    findRoutes(points: Point[], vehicles: Vehicles[], callback: (event: RoutesFoundEvent<DrivingInfo | MasstransitInfo>) => void): void;
    findMasstransitRoutes(points: Point[], callback: (event: RoutesFoundEvent<MasstransitInfo>) => void): void;
    findPedestrianRoutes(points: Point[], callback: (event: RoutesFoundEvent<MasstransitInfo>) => void): void;
    findDrivingRoutes(points: Point[], callback: (event: RoutesFoundEvent<DrivingInfo>) => void): void;
    fitAllMarkers(): void;
    getPolygonCoords(callback: (length: RouteLength) => void): void;
    getClosestPoint(point: Point, points: Point[], callback: (point: Point) => void): void;
    setTrafficVisible(isVisible: boolean): void;
    fitMarkers(points: Point[]): void;
    setCenter(center: {
        lon: number;
        lat: number;
        zoom?: number;
    }, zoom?: number, azimuth?: number, tilt?: number, duration?: number, animation?: Animation): void;
    setZoom(zoom: number, duration?: number, animation?: Animation): void;
    getCameraPosition(callback: (position: CameraPosition) => void): void;
    getVisibleRegion(callback: (VisibleRegion: VisibleRegion) => void): void;
    getScreenPoints(points: Point[], callback: (screenPoint: ScreenPoint) => void): void;
    getWorldPoints(points: ScreenPoint[], callback: (point: Point) => void): void;
    private _findRoutes;
    private getCommand;
    private processRoute;
    private processCameraPosition;
    private processVisibleRegion;
    private processRouteLength;
    private processClosestPoint;
    private processWorldToScreenPointsReceived;
    private processScreenToWorldPointsReceived;
    private resolveImageUri;
    private getProps;
    render(): React.JSX.Element;
}
