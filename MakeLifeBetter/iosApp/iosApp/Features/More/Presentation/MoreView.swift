import SwiftUI

struct MoreView: View {
    var onProfileClick: () -> Void
    var onAlarmsClick: () -> Void
    var onContactClick: () -> Void

    var body: some View {
        List {
            MoreMenuItem(
                icon: "person.circle",
                title: "Perfil",
                action: onProfileClick
            )

            MoreMenuItem(
                icon: "bell",
                title: "Notificações",
                action: onAlarmsClick
            )

            MoreMenuItem(
                icon: "briefcase",
                title: "Contrate",
                action: onContactClick
            )
        }
        .navigationTitle("Mais")
    }
}

private struct MoreMenuItem: View {
    let icon: String
    let title: String
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            HStack {
                Image(systemName: icon)
                    .foregroundColor(.accentColor)
                Text(title)
            }
        }
    }
}
