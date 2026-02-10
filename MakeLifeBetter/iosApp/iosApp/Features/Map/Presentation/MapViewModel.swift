//
//  MapViewModel.swift
//  iosApp
//

import Foundation
import Observation
import ComposeApp

@Observable
class MapViewModel {

    var eventLocation: EventLocation? = nil
    var isLoading: Bool = true
    var errorMessage: String? = nil

    private let sharedViewModel: SharedMapViewModelWrapper

    init() {
        sharedViewModel = SharedMapViewModelWrapper()
        setupObservers()
    }

    private func setupObservers() {
        sharedViewModel.observeEventLocationState(
            onIdle: { [weak self] in
                DispatchQueue.main.async {
                    self?.isLoading = false
                }
            },
            onLoading: { [weak self] in
                DispatchQueue.main.async {
                    self?.isLoading = true
                    self?.errorMessage = nil
                }
            },
            onSuccess: { [weak self] kotlinLocation in
                DispatchQueue.main.async {
                    self?.eventLocation = EventLocation(from: kotlinLocation)
                    self?.isLoading = false
                    self?.errorMessage = nil
                }
            },
            onError: { [weak self] message in
                DispatchQueue.main.async {
                    self?.isLoading = false
                    self?.errorMessage = message
                    // Show default location on error
                    self?.eventLocation = EventLocation(
                        id: "default",
                        name: "Evento",
                        address: "Endereco nao disponivel",
                        city: "Curitiba, Parana, Brasil",
                        latitude: -25.4284,
                        longitude: -49.2733,
                        contacts: []
                    )
                    print("MapViewModel error: \(message)")
                }
            }
        )
    }

    // MARK: - Actions

    func refreshEventLocation() {
        sharedViewModel.refreshEventLocation()
    }

    deinit {
        sharedViewModel.clear()
    }
}
