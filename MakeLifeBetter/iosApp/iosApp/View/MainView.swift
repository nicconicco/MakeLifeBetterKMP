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
    var loginViewModel: LoginViewModel
    @State var eventViewModel = EventViewModel()

    @State private var selectedItem: NavigationItem = .evento
    @State private var navigationPath = NavigationPath()

    var body: some View {
        @State var idiomaAtual: Idioma = .portugues

        var strings: AppStrings {
            Traducoes.obterStrings(idioma: idiomaAtual)
        }

        NavigationStack(path: $navigationPath) {
            TabView {
                SectionedListView(
                    viewModel: eventViewModel,
                    onItemClick: { event in
                        navigationPath.append(event)
                    }
                ).tabItem { Label("Eventos", systemImage: "calendar") }

                MeView(currentScreen: $currentScreen, strings: strings, viewModel: loginViewModel)
                    .tabItem { Label("Me", systemImage: "briefcase") }

                HireMeView()
                    .tabItem { Label("Contrate", systemImage: "briefcase") }
                    .tag(NavigationItem.contrate)
            }
            .navigationDestination(for: Event.self) { event in
                EventDetailScreen(event: event)
            }
        }
    }
}
