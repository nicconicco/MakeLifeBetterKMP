import SwiftUI

struct LoadingScreen: View {
    let strings: AppStrings

    var body: some View {
        VStack(spacing: 16) {
            Spacer()
            Text(strings.appName)
                .font(.title2)
                .fontWeight(.semibold)
                .foregroundColor(.blue)
            ProgressView()
                .progressViewStyle(CircularProgressViewStyle())
                .scaleEffect(1.2)
            Spacer()
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(Color(.systemBackground))
    }
}

#Preview {
    LoadingScreen(strings: Traducoes.portugues)
}
