package com.carlosnicolaugalves.makelifebetter.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreLocation.CLLocationCoordinate2DMake
import platform.MapKit.MKCoordinateRegionMakeWithDistance
import platform.MapKit.MKMapView
import platform.MapKit.MKPointAnnotation

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun MapView(
    latitude: Double,
    longitude: Double,
    modifier: Modifier
) {
    val coordinate = CLLocationCoordinate2DMake(latitude, longitude)
    val annotation = remember {
        MKPointAnnotation().apply {
            setCoordinate(coordinate)
            setTitle("Curitiba")
            setSubtitle("Local do Evento")
        }
    }

    UIKitView(
        factory = {
            MKMapView().apply {
                val region = MKCoordinateRegionMakeWithDistance(coordinate, 5000.0, 5000.0)
                setRegion(region, animated = false)
                addAnnotation(annotation)
            }
        },
        modifier = modifier
    )
}
