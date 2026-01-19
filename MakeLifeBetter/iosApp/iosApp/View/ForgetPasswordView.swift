import SwiftUI

struct ForgetPasswordView: View {
    @Binding var currentScreen: AppScreen
    let strings: AppStrings

    @State private var email: String = ""
    @State private var cpf: String = ""

    private var camposPreenchidos: Bool {
        !email.trimmingCharacters(in: .whitespaces).isEmpty &&
        !cpf.trimmingCharacters(in: .whitespaces).isEmpty
    }

    var body: some View {
        VStack(spacing: 16) {
            Spacer()

            Text(strings.esqueciSenhaTitulo)
                .font(.largeTitle)
                .fontWeight(.bold)
                .foregroundColor(.blue)

            Spacer()
                .frame(height: 24)

            TextField("\(strings.email) *", text: $email)
                .textFieldStyle(RoundedBorderTextFieldStyle())
                .autocapitalization(.none)
                .keyboardType(.emailAddress)

            TextField("\(strings.cpf) *", text: $cpf)
                .textFieldStyle(RoundedBorderTextFieldStyle())
                .keyboardType(.numberPad)

            HStack {
                Text(strings.camposObrigatorios)
                    .font(.caption)
                    .foregroundColor(.gray)
                Spacer()
            }

            Spacer()
                .frame(height: 16)

            Button(action: {
                currentScreen = .senhaTemp
            }) {
                Text(strings.confirmar)
                    .frame(maxWidth: .infinity)
                    .padding()
                    .background(camposPreenchidos ? Color.blue : Color.gray)
                    .foregroundColor(.white)
                    .cornerRadius(8)
            }
            .disabled(!camposPreenchidos)

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
    ForgetPasswordView(
        currentScreen: .constant(.esqueciSenha),
        strings: Traducoes.portugues
    )
}
