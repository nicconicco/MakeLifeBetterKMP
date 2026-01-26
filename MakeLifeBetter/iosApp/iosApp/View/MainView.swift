//
//  MainScreen.swift
//  iosApp
//
//  Created by Carlos Nicolau Galves on 22/01/26.
//


import SwiftUI
import ComposeApp

struct MainView: View {
    @Binding var currentScreen: AppScreen
    let strings: AppStrings
    @ObservedObject var loginViewModel: LoginViewModel
    @StateObject var eventViewModel = EventViewModel()

    @State private var selectedItem: NavigationItem = .evento
    @State private var selectedEvent: Event? = nil

    var body: some View {
        @State var idiomaAtual: Idioma = .portugues

        var strings: AppStrings {
            Traducoes.obterStrings(idioma: idiomaAtual)
        }

        VStack {
            if let selectedEvent = selectedEvent {
                EventDetailScreen(event: selectedEvent, onBackClick: {
                    self.selectedEvent = nil
                })
            } else {
                TabView {
                    SectionedListView(
                        viewModel: eventViewModel,
                        onItemClick: { event in
                            self.selectedEvent = event
                        }
                    ).tabItem { Label("Eventos", systemImage: "calendar") }
                    
                    MeView(currentScreen: $currentScreen, strings: strings, viewModel: loginViewModel)
                        .tabItem { Label("Me", systemImage: "briefcase") }
                 
                    HireMeView()
                        .tabItem { Label("Contrate", systemImage: "briefcase") }
                        .tag(NavigationItem.contrate)
                }
            }
        }
    }
}
