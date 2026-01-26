import SwiftUI

struct EventDetailScreen: View {
    var event: Event

    var body: some View {
        let emoji = getEmojiForCategory(categoria: event.categoria)

        ScrollView {
            VStack(spacing: 16) {
                // Header with large emoji
                VStack(alignment: .center, spacing: 16) {
                    Text(emoji)
                        .font(.system(size: 64))
                    Text(event.titulo)
                        .font(.title2)
                        .fontWeight(.bold)
                    Text(event.subtitulo)
                        .font(.subheadline)
                        .foregroundColor(.secondary)
                }
                .frame(maxWidth: .infinity)
                .padding(24)
                .background(Color.blue.opacity(0.1))
                .cornerRadius(12)

                // Event information
                VStack(alignment: .leading, spacing: 12) {
                    Text("Informacoes")
                        .font(.headline)
                        .fontWeight(.bold)

                    InfoRow(label: "Horario", value: event.hora)
                    InfoRow(label: "Local", value: event.lugar)
                    if !event.categoria.isEmpty {
                        InfoRow(label: "Categoria", value: getCategoryDisplayName(categoria: event.categoria))
                    }
                }
                .frame(maxWidth: .infinity, alignment: .leading)
                .padding(16)
                .background(Color(.systemBackground))
                .cornerRadius(12)
                .shadow(color: .black.opacity(0.08), radius: 4, x: 0, y: 2)

                // Description
                VStack(alignment: .leading, spacing: 8) {
                    Text("Descricao")
                        .font(.headline)
                        .fontWeight(.bold)
                    Text(event.descricao)
                        .font(.body)
                        .foregroundColor(.secondary)
                }
                .frame(maxWidth: .infinity, alignment: .leading)
                .padding(16)
                .background(Color(.systemBackground))
                .cornerRadius(12)
                .shadow(color: .black.opacity(0.08), radius: 4, x: 0, y: 2)

                // Category Tag
                if !event.categoria.isEmpty {
                    VStack(alignment: .leading, spacing: 8) {
                        Text("Categoria")
                            .font(.headline)
                            .fontWeight(.bold)
                        Text(getCategoryDisplayName(categoria: event.categoria))
                            .padding(.horizontal, 12)
                            .padding(.vertical, 6)
                            .foregroundColor(.white)
                            .background(Color.blue)
                            .cornerRadius(8)
                    }
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .padding(16)
                    .background(Color(.secondarySystemBackground))
                    .cornerRadius(12)
                }

                Spacer(minLength: 8)
            }
            .padding(16)
        }
        .background(Color(.systemGroupedBackground))
        .navigationTitle("Detalhes")
        .navigationBarTitleDisplayMode(.inline)
    }
}

struct InfoRow: View {
    var label: String
    var value: String

    var body: some View {
        HStack {
            Text(label)
                .font(.body)
                .foregroundColor(.secondary)
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
    case "cerimonia":
        return "🎭"
    case "intervalo":
        return "☕"
    case "palestra":
        return "🎤"
    case "refeicao":
        return "🍽️"
    case "workshop":
        return "🛠️"
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
    case "cerimonia":
        return "Cerimonia"
    case "intervalo":
        return "Intervalo"
    case "palestra":
        return "Palestra"
    case "refeicao":
        return "Refeicao"
    case "workshop":
        return "Workshop"
    default:
        return categoria.capitalized
    }
}
