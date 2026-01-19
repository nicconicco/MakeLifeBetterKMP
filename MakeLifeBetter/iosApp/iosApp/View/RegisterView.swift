import SwiftUI

struct RegisterView: View {
    @Binding var currentScreen: AppScreen
    @Binding var termosAceitos: Bool
    let strings: AppStrings

    @State private var nome: String = ""
    @State private var email: String = ""
    @State private var cpf: String = ""
    @State private var celular: String = ""

    private var camposPreenchidos: Bool {
        !nome.trimmingCharacters(in: .whitespaces).isEmpty &&
        !email.trimmingCharacters(in: .whitespaces).isEmpty &&
        !cpf.trimmingCharacters(in: .whitespaces).isEmpty &&
        !celular.trimmingCharacters(in: .whitespaces).isEmpty
    }

    private var podeCadastrar: Bool {
        camposPreenchidos && termosAceitos
    }

    var body: some View {
        VStack(spacing: 16) {
            Spacer()

            Text(strings.criarConta)
                .font(.largeTitle)
                .fontWeight(.bold)
                .foregroundColor(.blue)

            Spacer()
                .frame(height: 24)

            TextField("\(strings.nome) *", text: $nome)
                .textFieldStyle(RoundedBorderTextFieldStyle())

            TextField("\(strings.email) *", text: $email)
                .textFieldStyle(RoundedBorderTextFieldStyle())
                .autocapitalization(.none)
                .keyboardType(.emailAddress)

            TextField("\(strings.cpf) *", text: $cpf)
                .textFieldStyle(RoundedBorderTextFieldStyle())
                .keyboardType(.numberPad)

            TextField("\(strings.celular) *", text: $celular)
                .textFieldStyle(RoundedBorderTextFieldStyle())
                .keyboardType(.phonePad)

            HStack {
                Text(strings.camposObrigatorios)
                    .font(.caption)
                    .foregroundColor(.gray)
                Spacer()
            }

            HStack {
                Toggle("", isOn: .constant(termosAceitos))
                    .labelsHidden()
                    .fixedSize()
                    .disabled(true)

                Text(strings.aceitoTermos)
                    .font(.subheadline)
                    .foregroundColor(.blue)
                    .underline()
                    .padding(.leading, 5)
                    .onTapGesture {
                        currentScreen = .termos
                    }
            }
            .padding(.vertical, 8)

            Spacer()
                .frame(height: 16)

            Button(action: {
                currentScreen = .login
            }) {
                Text(strings.cadastrar)
                    .frame(maxWidth: .infinity)
                    .padding()
                    .background(podeCadastrar ? Color.blue : Color.gray)
                    .foregroundColor(.white)
                    .cornerRadius(8)
            }
            .disabled(!podeCadastrar)

            Button(strings.voltarLogin) {
                currentScreen = .login
            }
            .padding(.top, 8)

            Spacer()
        }
        .padding(.horizontal, 32)
    }
}

#Preview {
    RegisterView(
        currentScreen: .constant(.cadastro),
        termosAceitos: .constant(false),
        strings: Traducoes.portugues
    )
}
