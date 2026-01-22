//
//  MainScreen.swift
//  iosApp
//
//  Created by Carlos Nicolau Galves on 22/01/26.
//


import SwiftUI
import ComposeApp

private func createSectionsFromEvents(events: [Event]) -> [EventSection] {
    let sectionMap: [String: String] = [
        "agora": "O que tá rolando agora",
        "programado": "Ainda vai rolar",
        "novidade": "Novidades",
        "contato": "Canais de Contato",
        "cupom": "Cupons"
    ]
    
    return sectionMap.compactMap { (categoria, titulo) in
        let categoryEvents = events.filter { $0.categoria == categoria }
        if !categoryEvents.isEmpty {
            return EventSection(titulo: titulo, eventos: categoryEvents)
        } else {
            return nil
        }
    }
}

struct MainView: View {
    @Binding var currentScreen: AppScreen
    let strings: AppStrings
    @ObservedObject var loginViewModel: LoginViewModel
    //    @StateObject var eventViewModel = SharedEventViewModelWrapper()
    //    @StateObject var notificationViewModel = SharedNotificationViewModel()
    //    @StateObject var chatViewModel = SharedChatViewModel()
    
    @State private var selectedItem: NavigationItem = .evento
    @State private var selectedEvent: Event? = nil
    
    var body: some View {
        let currentUser = loginViewModel.currentUser
        let profileUpdateState = loginViewModel.profileUpdateState
        let passwordChangeState = loginViewModel.passwordChangeState
        
        //        let eventSections = eventViewModel.eventSections
        //        let sectionsState = eventViewModel.sectionsState
        //        let shouldRequestPermission = notificationViewModel.shouldRequestPermission
        
        //        let isLoading = sectionsState is EventSectionsResult.Loading
        
        // Handle notification permission request
        //        NotificationPermissionHandler(
        //            shouldRequest: shouldRequestPermission,
        //            onPermissionResult: { granted in
        //                notificationViewModel.onPermissionResult(granted: granted)
        //            },
        //            onRequestHandled: {
        //                notificationViewModel.onPermissionRequestHandled()
        //            }
        //        )
        
        // Schedule notifications when events are loaded
        //        if let sectionsState = sectionsState as? EventSectionsResult.Success {
        //            let allEvents = sectionsState.eventSections.flatMap { $0.eventos }
        //            notificationViewModel.scheduleNotificationsForEvents(allEvents)
        //        }
        
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
                    HomeView(currentScreen: $currentScreen, strings: strings, viewModel: loginViewModel)
                        .tabItem { Label("Contrate", systemImage: "briefcase") }
                    SectionedListView(
                        sections: createSectionsFromEvents(events: getSampleEvents()),
                        isLoading: false,
                        onItemClick: { event in
                            self.selectedEvent = event
                        }
                    )
                    .tabItem { Label("Eventos", systemImage: "star") }
                    
                    HireMeView()
                        .tabItem { Label("Contrate", systemImage: "briefcase") }
                        .tag(NavigationItem.contrate)
                }
                
                //                TabView {
                //                    SectionedListView(
                //                        sections: createSectionsFromEvents(events: getSampleEvents()),
                //                        isLoading: false,
                //                        onItemClick: { event in
                //                            self.selectedEvent = event
                //                        }
                //                    )
                //                    .tabItem { Label("Eventos", systemImage: "star") }
                //
                //
                //                    //                    MapScreen()
                //                    //                        .tabItem { Label("Mapa", systemImage: "map") }
                //                    //                        .tag(NavigationItem.mapa)
                //                    ProfileView(currentUser: currentUser, profileUpdateState: profileUpdateState, passwordChangeState: passwordChangeState,
                //                                           onSaveClick: { username, email in
                //                        viewModel.updateProfile(username: username, email: email)
                //                    },
                //                                           onChangePasswordClick: { current, new, confirm in
                //                        viewModel.changePassword(current: current, new: new, confirm: confirm)
                //                    },
                //                                           onLogoutClick: {
                //                        viewModel.logout()
                //                    })
                //                    .tabItem { Label("Perfil", systemImage: "person") }
                //                    .tag(NavigationItem.perfil)
                //
                //                    //                    ChatScreen(currentUsername: currentUser?.username ?? "Usuario", chatViewModel: chatViewModel)
                //                    //                        .tabItem { Label("Chat", systemImage: "message") }
                //                    //                        .tag(NavigationItem.chat)
                //
                //                    //                    NotificationScreen(viewModel: notificationViewModel)
                //                    //                        .tabItem { Label("Notificações", systemImage: "bell") }
                //                    //                        .tag(NavigationItem.notificacoes)
                //
                //                                    HireMeView()
                //                                        .tabItem { Label("Contrate", systemImage: "briefcase") }
                //                                        .tag(NavigationItem.contrate)
                //                }
                //            }
            }
        }
        .onAppear {
            // Any additional setup can be done here
        }
    }
}
