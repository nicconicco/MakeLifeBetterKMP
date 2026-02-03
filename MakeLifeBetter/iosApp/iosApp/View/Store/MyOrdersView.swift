import SwiftUI
import ComposeApp

struct MyOrdersView: View {
    var viewModel: StoreViewModel
    var onBackClick: () -> Void

    var body: some View {
        VStack {
            if viewModel.ordersState == .loading {
                ProgressView()
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
            } else if viewModel.orders.isEmpty {
                VStack(spacing: 16) {
                    Image(systemName: "bag")
                        .font(.system(size: 64))
                        .foregroundColor(.secondary)
                    Text("Voce ainda nao fez nenhum pedido")
                        .foregroundColor(.secondary)
                }
                .frame(maxWidth: .infinity, maxHeight: .infinity)
            } else {
                ScrollView {
                    LazyVStack(spacing: 12) {
                        ForEach(viewModel.orders, id: \.id) { order in
                            OrderCardView(order: order)
                        }
                    }
                    .padding(.horizontal, 16)
                    .padding(.vertical, 8)
                }
            }
        }
        .navigationTitle("Meus Pedidos")
        .navigationBarTitleDisplayMode(.inline)
        .navigationBarBackButtonHidden(true)
        .toolbar {
            ToolbarItem(placement: .navigationBarLeading) {
                Button(action: onBackClick) {
                    HStack(spacing: 4) {
                        Image(systemName: "chevron.left")
                        Text("Voltar")
                    }
                }
            }
        }
        .onAppear {
            viewModel.loadOrders()
        }
    }
}

struct OrderCardView: View {
    let order: ComposeApp.Order

    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            HStack {
                Text(order.id)
                    .font(.headline)
                    .fontWeight(.bold)
                Spacer()
                StatusChipView(status: order.status)
            }

            Text("Itens:")
                .font(.caption)
                .foregroundColor(.secondary)

            ForEach(order.items, id: \.id) { item in
                HStack {
                    Text("\(item.quantidade)x \(item.product.nome)")
                        .font(.subheadline)
                    Spacer()
                    Text(String(format: "R$ %.2f", item.product.preco * Double(item.quantidade)))
                        .font(.subheadline)
                }
            }

            Divider()

            HStack {
                Text("Total:")
                    .font(.subheadline)
                    .fontWeight(.bold)
                Spacer()
                Text(String(format: "R$ %.2f", order.totalPrice))
                    .font(.subheadline)
                    .fontWeight(.bold)
                    .foregroundColor(.blue)
            }

            Text(formatDate(timestamp: order.createdAt))
                .font(.caption)
                .foregroundColor(.secondary)
        }
        .padding(16)
        .background(Color(.systemBackground))
        .cornerRadius(12)
        .shadow(color: .black.opacity(0.08), radius: 4, x: 0, y: 2)
    }

    private func formatDate(timestamp: Int64) -> String {
        let date = Date(timeIntervalSince1970: Double(timestamp) / 1000.0)
        let formatter = DateFormatter()
        formatter.dateFormat = "dd/MM/yyyy HH:mm"
        return formatter.string(from: date)
    }
}

struct StatusChipView: View {
    let status: ComposeApp.OrderStatus

    var backgroundColor: Color {
        switch status {
        case .pending:
            return Color.orange.opacity(0.2)
        case .confirmed:
            return Color.blue.opacity(0.2)
        case .processing:
            return Color.purple.opacity(0.2)
        case .shipped:
            return Color.green.opacity(0.2)
        case .delivered:
            return Color.green.opacity(0.3)
        case .cancelled:
            return Color.red.opacity(0.2)
        default:
            return Color.gray.opacity(0.2)
        }
    }

    var textColor: Color {
        switch status {
        case .pending:
            return Color.orange
        case .confirmed:
            return Color.blue
        case .processing:
            return Color.purple
        case .shipped:
            return Color.green
        case .delivered:
            return Color(red: 0.1, green: 0.4, blue: 0.1)
        case .cancelled:
            return Color.red
        default:
            return Color.gray
        }
    }

    var statusText: String {
        switch status {
        case .pending:
            return "Pendente"
        case .confirmed:
            return "Confirmado"
        case .processing:
            return "Processando"
        case .shipped:
            return "Enviado"
        case .delivered:
            return "Entregue"
        case .cancelled:
            return "Cancelado"
        default:
            return "Desconhecido"
        }
    }

    var body: some View {
        Text(statusText)
            .font(.caption)
            .fontWeight(.medium)
            .foregroundColor(textColor)
            .padding(.horizontal, 12)
            .padding(.vertical, 6)
            .background(backgroundColor)
            .cornerRadius(8)
    }
}

#Preview {
    NavigationStack {
        MyOrdersView(
            viewModel: StoreViewModel(),
            onBackClick: {}
        )
    }
}
