//
//  MapView.swift
//  iosApp
//

import SwiftUI
import MapKit

struct MapView: View {
    @State var viewModel = MapViewModel()

    var body: some View {
        if viewModel.isLoading {
            ProgressView()
                .frame(maxWidth: .infinity, maxHeight: .infinity)
        } else if let location = viewModel.eventLocation {
            MapContentView(eventLocation: location)
        } else {
            MapContentView(
                eventLocation: EventLocation(
                    id: "default",
                    name: "Evento",
                    address: "Carregando...",
                    city: "Curitiba, Parana, Brasil",
                    latitude: -25.4284,
                    longitude: -49.2733,
                    contacts: []
                )
            )
        }
    }
}

// MARK: - Map Content

private struct MapContentView: View {
    let eventLocation: EventLocation

    var body: some View {
        ScrollView {
            VStack(spacing: 0) {
                // Header Card
                VStack(alignment: .leading, spacing: 4) {
                    Text("Localizacao do Evento")
                        .font(.headline)
                        .fontWeight(.bold)

                    Text(eventLocation.city)
                        .font(.subheadline)
                        .foregroundColor(.secondary)
                }
                .frame(maxWidth: .infinity, alignment: .leading)
                .padding(16)
                .background(Color(.systemGray6))
                .cornerRadius(12)
                .padding(.horizontal, 16)
                .padding(.top, 16)

                // Map
                MapKitView(
                    latitude: eventLocation.latitude,
                    longitude: eventLocation.longitude
                )
                .frame(height: 300)
                .cornerRadius(12)
                .padding(.horizontal, 16)
                .padding(.top, 16)

                // Address
                Text(eventLocation.address)
                    .font(.headline)
                    .fontWeight(.bold)
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .padding(.horizontal, 16)
                    .padding(.top, 16)

                // Contacts
                if !eventLocation.contacts.isEmpty {
                    ForEach(Array(eventLocation.contacts.enumerated()), id: \.element.id) { index, contact in
                        ContactCardView(index: index + 1, contact: contact)
                    }
                } else {
                    // Placeholder when no contacts
                    VStack(alignment: .leading, spacing: 4) {
                        Text("Contatos")
                            .font(.headline)
                            .fontWeight(.bold)

                        Text("Nenhum contato disponivel")
                            .font(.subheadline)
                            .foregroundColor(.secondary)
                    }
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .padding(16)
                    .background(Color(.systemGray6))
                    .cornerRadius(12)
                    .padding(.horizontal, 16)
                    .padding(.top, 16)
                }
            }
            .padding(.bottom, 16)
        }
        .background(Color.white)
    }
}

// MARK: - Contact Card

private struct ContactCardView: View {
    let index: Int
    let contact: EventContact

    var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            Text("\(index) - \(contact.name)")
                .font(.headline)
                .fontWeight(.bold)

            Text(contact.phone)
                .font(.subheadline)
                .foregroundColor(.secondary)
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(16)
        .background(Color(.systemGray6))
        .cornerRadius(12)
        .padding(.horizontal, 16)
        .padding(.top, 16)
    }
}

// MARK: - MapKit UIViewRepresentable

private struct MapKitView: UIViewRepresentable {
    let latitude: Double
    let longitude: Double

    func makeUIView(context: Context) -> MKMapView {
        let mapView = MKMapView()
        mapView.isScrollEnabled = true
        mapView.isZoomEnabled = true
        return mapView
    }

    func updateUIView(_ mapView: MKMapView, context: Context) {
        let coordinate = CLLocationCoordinate2D(latitude: latitude, longitude: longitude)
        let region = MKCoordinateRegion(
            center: coordinate,
            latitudinalMeters: 5000,
            longitudinalMeters: 5000
        )
        mapView.setRegion(region, animated: true)

        mapView.removeAnnotations(mapView.annotations)
        let annotation = MKPointAnnotation()
        annotation.coordinate = coordinate
        mapView.addAnnotation(annotation)
    }
}

#Preview {
    MapView()
}
