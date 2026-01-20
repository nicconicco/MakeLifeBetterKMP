import SwiftUI

struct HomeView: View {
    @Binding var currentScreen: AppScreen
    let strings: AppStrings
    @ObservedObject var viewModel: LoginViewModel

    var body: some View {
        VStack(spacing: 24) {
            Spacer()

            Text(strings.appName)
                .font(.largeTitle)
                .fontWeight(.bold)
                .foregroundColor(.blue)

            if let user = viewModel.currentUser {
                Text("\(strings.bemVindo), \(user.username)!")
                    .font(.title2)
                    .foregroundColor(.primary)

                Text(user.email)
                    .font(.subheadline)
                    .foregroundColor(.gray)
            }

            Spacer()

            Button(action: {
                viewModel.logout()
                currentScreen = .login
            }) {
                Text(strings.sair)
                    .frame(maxWidth: .infinity)
                    .padding()
                    .background(Color.red)
                    .foregroundColor(.white)
                    .cornerRadius(8)
            }
        }
        .padding(.horizontal, 32)
    }
}

#Preview {
    HomeView(
        currentScreen: .constant(.home),
        strings: Traducoes.portugues,
        viewModel: LoginViewModel()
    )
}
