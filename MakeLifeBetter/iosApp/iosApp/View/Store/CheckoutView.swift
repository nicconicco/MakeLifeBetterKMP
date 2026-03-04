import SwiftUI
import ComposeApp
import StripePaymentSheet

struct CheckoutView: View {
    var viewModel: StoreViewModel
    var onBackClick: () -> Void
    var onConfirmOrder: () -> Void

    // Address state
    @State private var street: String = ""
    @State private var number: String = ""
    @State private var complement: String = ""
    @State private var neighborhood: String = ""
    @State private var city: String = ""
    @State private var state: String = ""
    @State private var zipCode: String = ""

    // Stripe Payment Sheet
    @State private var paymentSheet: PaymentSheet?
    @State private var pendingPaymentIntentId: String?

    private var isAddressValid: Bool {
        !street.isEmpty &&
        !number.isEmpty &&
        !neighborhood.isEmpty &&
        !city.isEmpty &&
        !state.isEmpty &&
        zipCode.count >= 8
    }

    var body: some View {
        VStack(spacing: 0) {
            // Header with back button
            HStack {
                Button(action: onBackClick) {
                    HStack(spacing: 4) {
                        Image(systemName: "chevron.left")
                            .font(.system(size: 16, weight: .semibold))
                        Text("Voltar")
                    }
                    .foregroundColor(.blue)
                }

                Spacer()

                Text("Finalizar Pedido")
                    .font(.headline)
                    .fontWeight(.bold)

                Spacer()

                // Invisible spacer to center the title
                HStack(spacing: 4) {
                    Image(systemName: "chevron.left")
                        .font(.system(size: 16, weight: .semibold))
                    Text("Voltar")
                }
                .opacity(0)
            }
            .padding(.horizontal, 16)
            .padding(.vertical, 12)
            .background(Color(.systemBackground))

            ScrollView {
                VStack(spacing: 16) {
                    // Address Section
                    VStack(alignment: .leading, spacing: 16) {
                        HStack {
                            Image(systemName: "house.fill")
                                .foregroundColor(.blue)
                            Text("Endereco de Entrega")
                                .font(.headline)
                                .fontWeight(.bold)
                        }

                        VStack(alignment: .leading, spacing: 4) {
                            Text("CEP").font(.caption).foregroundColor(.secondary)
                            TextField("00000-000", text: $zipCode)
                                .textFieldStyle(.roundedBorder)
                                .keyboardType(.numberPad)
                                .onChange(of: zipCode) { _, newValue in
                                    zipCode = formatCep(newValue)
                                }
                        }

                        VStack(alignment: .leading, spacing: 4) {
                            Text("Rua").font(.caption).foregroundColor(.secondary)
                            TextField("Nome da rua", text: $street)
                                .textFieldStyle(.roundedBorder)
                        }

                        HStack(spacing: 12) {
                            VStack(alignment: .leading, spacing: 4) {
                                Text("Numero").font(.caption).foregroundColor(.secondary)
                                TextField("123", text: $number)
                                    .textFieldStyle(.roundedBorder)
                                    .keyboardType(.numberPad)
                            }

                            VStack(alignment: .leading, spacing: 4) {
                                Text("Complemento").font(.caption).foregroundColor(.secondary)
                                TextField("Apto, Bloco", text: $complement)
                                    .textFieldStyle(.roundedBorder)
                            }
                        }

                        VStack(alignment: .leading, spacing: 4) {
                            Text("Bairro").font(.caption).foregroundColor(.secondary)
                            TextField("Bairro", text: $neighborhood)
                                .textFieldStyle(.roundedBorder)
                        }

                        HStack(spacing: 12) {
                            VStack(alignment: .leading, spacing: 4) {
                                Text("Cidade").font(.caption).foregroundColor(.secondary)
                                TextField("Cidade", text: $city)
                                    .textFieldStyle(.roundedBorder)
                            }

                            VStack(alignment: .leading, spacing: 4) {
                                Text("UF").font(.caption).foregroundColor(.secondary)
                                TextField("SP", text: $state)
                                    .textFieldStyle(.roundedBorder)
                                    .textCase(.uppercase)
                                    .onChange(of: state) { _, newValue in
                                        state = String(newValue.prefix(2)).uppercased()
                                    }
                            }
                            .frame(width: 80)
                        }
                    }
                    .padding(16)
                    .background(Color(.systemBackground))
                    .cornerRadius(16)
                    .shadow(color: .black.opacity(0.08), radius: 4, x: 0, y: 2)

                    // Payment Section (Stripe)
                    VStack(alignment: .leading, spacing: 16) {
                        HStack {
                            Image(systemName: "creditcard.fill")
                                .foregroundColor(.blue)
                            Text("Pagamento")
                                .font(.headline)
                                .fontWeight(.bold)
                        }

                        Button(action: {
                            viewModel.createPaymentIntent()
                        }) {
                            HStack {
                                if viewModel.paymentIntentState == .loading {
                                    ProgressView()
                                        .tint(.white)
                                    Text("Processando...")
                                } else {
                                    Image(systemName: "lock.fill")
                                    Text("Pagar com Cartao")
                                }
                            }
                            .frame(maxWidth: .infinity)
                            .padding(.vertical, 14)
                        }
                        .buttonStyle(.borderedProminent)
                        .disabled(!isAddressValid || viewModel.paymentIntentState == .loading)

                        if case .error(let message) = viewModel.paymentIntentState {
                            Text(message)
                                .font(.caption)
                                .foregroundColor(.red)
                        }

                        HStack(spacing: 4) {
                            Image(systemName: "lock.fill")
                                .font(.caption2)
                            Text("Pagamento seguro via Stripe")
                                .font(.caption)
                        }
                        .foregroundColor(.secondary)
                        .frame(maxWidth: .infinity)
                    }
                    .padding(16)
                    .background(Color(.systemBackground))
                    .cornerRadius(16)
                    .shadow(color: .black.opacity(0.08), radius: 4, x: 0, y: 2)

                    // Order Summary
                    VStack(alignment: .leading, spacing: 12) {
                        Text("Resumo do Pedido")
                            .font(.headline)
                            .fontWeight(.bold)

                        ForEach(viewModel.cart.items, id: \.id) { item in
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
                            Text("Subtotal")
                                .font(.subheadline)
                            Spacer()
                            Text(String(format: "R$ %.2f", viewModel.cart.totalPrice))
                                .font(.subheadline)
                        }

                        HStack {
                            Text("Frete")
                                .font(.subheadline)
                            Spacer()
                            Text("Gratis")
                                .font(.subheadline)
                                .foregroundColor(.blue)
                        }
                    }
                    .padding(16)
                    .background(Color(.secondarySystemBackground))
                    .cornerRadius(16)

                    Spacer().frame(height: 100)
                }
                .padding(16)
            }

            // Bottom bar - total only
            VStack(spacing: 16) {
                HStack {
                    Text("Total")
                        .font(.title2)
                        .fontWeight(.bold)
                    Spacer()
                    Text(String(format: "R$ %.2f", viewModel.cart.totalPrice))
                        .font(.title2)
                        .fontWeight(.bold)
                        .foregroundColor(.blue)
                }
            }
            .padding(16)
            .background(Color(.systemBackground))
            .shadow(color: .black.opacity(0.1), radius: 8, x: 0, y: -4)
        }
        .navigationBarHidden(true)
        .onChange(of: viewModel.paymentIntentState) { _, newState in
            if newState == .success, let info = viewModel.paymentIntentInfo {
                presentPaymentSheet(info: info)
            }
        }
        .alert("Atencao", isPresented: .constant(viewModel.errorMessage != nil)) {
            Button("OK") {
                viewModel.clearError()
            }
        } message: {
            Text(viewModel.errorMessage ?? "")
        }
    }

    private func presentPaymentSheet(info: PaymentIntentInfo) {
        var configuration = PaymentSheet.Configuration()
        configuration.merchantDisplayName = "Make Life Better"
        configuration.customer = .init(
            id: info.customerId,
            ephemeralKeySecret: info.ephemeralKey
        )
        configuration.allowsDelayedPaymentMethods = false

        let sheet = PaymentSheet(
            paymentIntentClientSecret: info.clientSecret,
            configuration: configuration
        )

        pendingPaymentIntentId = info.clientSecret

        sheet.present(from: UIApplication.shared.topViewController()) { result in
            switch result {
            case .completed:
                let address = Address(
                    street: street,
                    number: number,
                    complement: complement,
                    neighborhood: neighborhood,
                    city: city,
                    state: state,
                    zipCode: zipCode
                )
                if let intentId = pendingPaymentIntentId {
                    viewModel.checkoutAfterPayment(address: address, stripePaymentIntentId: intentId)
                    onConfirmOrder()
                }
                viewModel.resetPaymentIntentState()
                pendingPaymentIntentId = nil
            case .canceled:
                viewModel.resetPaymentIntentState()
                pendingPaymentIntentId = nil
            case .failed(let error):
                viewModel.errorMessage = error.localizedDescription
                viewModel.resetPaymentIntentState()
                pendingPaymentIntentId = nil
            }
        }
    }

    private func formatCep(_ input: String) -> String {
        let digitsOnly = input.filter { $0.isNumber }
        let limited = String(digitsOnly.prefix(8))
        if limited.count <= 5 {
            return limited
        } else {
            return "\(limited.prefix(5))-\(limited.dropFirst(5))"
        }
    }
}

// MARK: - UIApplication extension to get top view controller
extension UIApplication {
    func topViewController(base: UIViewController? = nil) -> UIViewController {
        let base = base ?? connectedScenes
            .compactMap { $0 as? UIWindowScene }
            .flatMap { $0.windows }
            .first(where: { $0.isKeyWindow })?.rootViewController

        if let nav = base as? UINavigationController {
            return topViewController(base: nav.visibleViewController)
        }
        if let tab = base as? UITabBarController, let selected = tab.selectedViewController {
            return topViewController(base: selected)
        }
        if let presented = base?.presentedViewController {
            return topViewController(base: presented)
        }
        return base ?? UIViewController()
    }
}

#Preview {
    NavigationStack {
        CheckoutView(
            viewModel: StoreViewModel(),
            onBackClick: {},
            onConfirmOrder: {}
        )
    }
}
