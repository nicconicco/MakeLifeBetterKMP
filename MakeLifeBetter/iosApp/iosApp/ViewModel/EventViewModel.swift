//
//  EventViewModel.swift
//  iosApp
//

import Foundation
import Observation
import ComposeApp

@Observable
class EventViewModel {

    var sections: [EventSection] = []
    var isLoading: Bool = true

    private let sharedViewModel: SharedEventViewModelWrapper

    init() {
        sharedViewModel = SharedEventViewModelWrapper()
        setupObservers()
    }

    private func setupObservers() {
        sharedViewModel.observeSectionsState(
            onIdle: { [weak self] in
                DispatchQueue.main.async {
                    self?.isLoading = false
                }
            },
            onLoading: { [weak self] in
                DispatchQueue.main.async {
                    self?.isLoading = true
                }
            },
            onSuccess: { [weak self] kotlinSections in
                DispatchQueue.main.async {
                    self?.sections = kotlinSections.map { EventSection(from: $0 as! ComposeApp.EventSection) }
                    self?.isLoading = false
                }
            },
            onError: { [weak self] message in
                DispatchQueue.main.async {
                    self?.isLoading = false
                    print("EventViewModel error: \(message)")
                }
            }
        )
    }

    // MARK: - Actions

    func refreshSections() {
        sharedViewModel.refreshSections()
    }

    deinit {
        sharedViewModel.clear()
    }
}
