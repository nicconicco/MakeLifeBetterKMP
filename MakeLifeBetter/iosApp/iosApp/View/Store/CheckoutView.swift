import SwiftUI
import ComposeApp

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

    // Payment state
    @State private var cardNumber: String = ""
    @State private var cardHolder: String = ""
    @State private var expiryDate: String = ""
    @State private var cvv: String = ""

    private var isAddressValid: Bool {
        !street.isEmpty &&
        !number.isEmpty &&
        !neighborhood.isEmpty &&
        !city.isEmpty &&
        !state.isEmpty &&
        zipCode.count >= 8
    }

    private var isPaymentValid: Bool {
        cardNumber.replacingOccurrences(of: " ", with: "").count == 16 &&
        !cardHolder.isEmpty &&
        expiryDate.count == 5 &&
        cvv.count >= 3
    }

    private var isFormValid: Bool {
        isAddressValid && isPaymentValid
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

                    // Payment Section
                    VStack(alignment: .leading, spacing: 16) {
                        HStack {
                            Image(systemName: "creditcard.fill")
                                .foregroundColor(.blue)
                            Text("Dados do Cartao")
                                .font(.headline)
                                .fontWeight(.bold)
                        }

                        VStack(alignment: .leading, spacing: 4) {
                            Text("Numero do Cartao").font(.caption).foregroundColor(.secondary)
                            TextField("0000 0000 0000 0000", text: $cardNumber)
                                .textFieldStyle(.roundedBorder)
                                .keyboardType(.numberPad)
                                .onChange(of: cardNumber) { _, newValue in
                                    cardNumber = formatCardNumber(newValue)
                                }
                        }

                        VStack(alignment: .leading, spacing: 4) {
                            Text("Nome no Cartao").font(.caption).foregroundColor(.secondary)
                            TextField("NOME COMO ESTA NO CARTAO", text: $cardHolder)
                                .textFieldStyle(.roundedBorder)
                                .textCase(.uppercase)
                        }

                        HStack(spacing: 12) {
                            VStack(alignment: .leading, spacing: 4) {
                                Text("Validade").font(.caption).foregroundColor(.secondary)
                                TextField("MM/AA", text: $expiryDate)
                                    .textFieldStyle(.roundedBorder)
                                    .keyboardType(.numberPad)
                                    .onChange(of: expiryDate) { _, newValue in
                                        expiryDate = formatExpiryDate(newValue)
                                    }
                            }

                            VStack(alignment: .leading, spacing: 4) {
                                Text("CVV").font(.caption).foregroundColor(.secondary)
                                SecureField("***", text: $cvv)
                                    .textFieldStyle(.roundedBorder)
                                    .keyboardType(.numberPad)
                                    .onChange(of: cvv) { _, newValue in
                                        cvv = String(newValue.filter { $0.isNumber }.prefix(4))
                                    }
                            }
                            .frame(width: 100)
                        }
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

            // Bottom bar
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

                Button(action: {
                    let address = Address(
                        street: street,
                        number: number,
                        complement: complement,
                        neighborhood: neighborhood,
                        city: city,
                        state: state,
                        zipCode: zipCode
                    )
                    let payment = PaymentInfo(
                        cardNumber: cardNumber,
                        cardHolder: cardHolder,
                        expiryDate: expiryDate,
                        cvv: cvv
                    )
                    viewModel.checkoutWithInfo(address: address, payment: payment)
                    onConfirmOrder()
                }) {
                    HStack {
                        Image(systemName: "lock.fill")
                        Text("Confirmar Pagamento")
                    }
                    .frame(maxWidth: .infinity)
                    .padding(.vertical, 14)
                }
                .buttonStyle(.borderedProminent)
                .disabled(!isFormValid)
            }
            .padding(16)
            .background(Color(.systemBackground))
            .shadow(color: .black.opacity(0.1), radius: 8, x: 0, y: -4)
        }
        .navigationBarHidden(true)
    }

    private func formatCardNumber(_ input: String) -> String {
        let digitsOnly = input.filter { $0.isNumber }
        let limited = String(digitsOnly.prefix(16))
        var result = ""
        for (index, char) in limited.enumerated() {
            if index > 0 && index % 4 == 0 {
                result += " "
            }
            result.append(char)
        }
        return result
    }

    private func formatExpiryDate(_ input: String) -> String {
        let digitsOnly = input.filter { $0.isNumber }
        let limited = String(digitsOnly.prefix(4))
        if limited.count <= 2 {
            return limited
        } else {
            return "\(limited.prefix(2))/\(limited.dropFirst(2))"
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

#Preview {
    NavigationStack {
        CheckoutView(
            viewModel: StoreViewModel(),
            onBackClick: {},
            onConfirmOrder: {}
        )
    }
}
