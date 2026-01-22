import SwiftUI
import ComposeApp

struct EventDetailScreen: View {
    var event: Event
    var onBackClick: () -> Void

    var body: some View {
        let emoji = getEmojiForCategory(categoria: event.categoria)

        NavigationView {
            ScrollView {
                VStack(spacing: 16) {
                    // Header with large emoji
                    VStack(alignment: .center, spacing: 16) {
                        Text(emoji)
                            .font(.system(size: 64))
                        Text(event.titulo)
                            .font(.headline)
                            .fontWeight(.bold)
                        Text(event.subtitulo)
                            .font(.subheadline)
                            .foregroundColor(Color.gray.opacity(0.8))
                    }
                    .padding(24)
                    .background(Color.blue.opacity(0.1)) // Background color for card effect
                    .cornerRadius(12) // Rounded corners
                    .padding(.horizontal)

                    // Event information
                    VStack(spacing: 12) {
                        Text("Informações")
                            .font(.title2)
                            .fontWeight(.bold)

                        InfoRow(label: "Horário", value: event.hora)
                        InfoRow(label: "Local", value: event.lugar)
                        if !event.categoria.isEmpty {
                            InfoRow(label: "Categoria", value: getCategoryDisplayName(categoria: event.categoria))
                        }
                    }
                    .padding()
                    .background(Color.white) // Background color for card effect
                    .cornerRadius(12) // Rounded corners
                    .shadow(color: Color.black.opacity(0.1), radius: 5, x: 0, y: 2) // Shadow effect
                    .padding(.horizontal)

                    // Description
                    VStack(spacing: 8) {
                        Text("Descrição")
                            .font(.title2)
                            .fontWeight(.bold)
                        Text(event.descricao)
                            .font(.body)
                            .foregroundColor(Color.black.opacity(0.8))
                    }
                    .padding()
                    .background(Color.white) // Background color for card effect
                    .cornerRadius(12) // Rounded corners
                    .shadow(color: Color.black.opacity(0.1), radius: 5, x: 0, y: 2) // Shadow effect
                    .padding(.horizontal)

                    // Category Tag
                    if !event.categoria.isEmpty {
                        VStack(spacing: 8) {
                            Text("Categoria")
                                .font(.title2)
                                .fontWeight(.bold)
                            Text(getCategoryDisplayName(categoria: event.categoria))
                                .padding(.horizontal, 12)
                                .padding(.vertical, 6)
                                .foregroundColor(.white)
                                .background(Color.blue)
                                .cornerRadius(8)
                        }
                        .padding()
                        .background(Color.white) // Background color for card effect
                        .cornerRadius(12) // Rounded corners
                        .shadow(color: Color.black.opacity(0.1), radius: 5, x: 0, y: 2) // Shadow effect
                        .padding(.horizontal)
                    }

                    Spacer(minLength: 8)
                }
                .padding(.vertical)
            }
            .navigationBarTitle("Detalhes", displayMode: .inline)
            .navigationBarItems(leading: Button(action: onBackClick) {
                Image(systemName: "arrow.left")
                    .foregroundColor(.blue)
            })
        }
    }
}

struct InfoRow: View {
    var label: String
    var value: String

    var body: some View {
        HStack {
            Text(label)
                .font(.body)
                .foregroundColor(Color.gray.opacity(0.6))
            Spacer()
            Text(value)
                .font(.body)
                .fontWeight(.medium)
        }
    }
}

func getEmojiForCategory(categoria: String) -> String {
    switch categoria.lowercased() {
    case "agora":
        return "🔴"
    case "programado":
        return "📅"
    case "novidade":
        return "🚀"
    case "contato":
        return "💬"
    case "cupom":
        return "🎟️"
    default:
        return "📌"
    }
}

func getCategoryDisplayName(categoria: String) -> String {
    switch categoria.lowercased() {
    case "agora":
        return "Acontecendo agora"
    case "programado":
        return "Programado"
    case "novidade":
        return "Novidade"
    case "contato":
        return "Contato"
    case "cupom":
        return "Cupom"
    default:
        return categoria.capitalized
    }
}
