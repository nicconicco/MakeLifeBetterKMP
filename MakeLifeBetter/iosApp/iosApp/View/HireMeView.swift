import SwiftUI

struct HireMeView: View {
    var body: some View {
        ScrollView {
            VStack(spacing: 24) {
                Spacer().frame(height: 16)

                // Header Section
                Text("Contrate-me!")
                    .font(.system(size: 36, weight: .bold))
                    .foregroundColor(Color.primary)
                    .multilineTextAlignment(.center)

                Text("Desenvolvedor Mobile iOS e Android Nativos (Swift e Kotlin)")
                    .font(.system(size: 18, weight: .medium))
                    .foregroundColor(Color.secondary)
                    .multilineTextAlignment(.center)

                Divider()
                    .frame(width: 100)
                    .padding(.vertical, 8)
                    .background(Color.primary)

                // Experience Section using RoundedRectangle as a Card
                VStack(spacing: 12) {
                    HStack(spacing: 12) {
                        Circle()
                            .fill(Color.blue.opacity(0.2))
                            .frame(width: 48, height: 48)
                            .overlay(Text("📱").font(.system(size: 24)))

                        VStack(alignment: .leading) {
                            Text("Experiência")
                                .font(.system(size: 20, weight: .bold))
                                .foregroundColor(Color.black)

                            Text("10 anos")
                                .font(.system(size: 14, weight: .semibold))
                                .foregroundColor(Color.blue)
                        }
                    }

                    Divider().padding(.vertical, 4)

                    Text("Desenvolvedor de aplicativos nativos Android com 10 anos de experiência. Especializado em Kotlin, Jetpack Compose e arquiteturas modernas.")
                        .font(.system(size: 15))
                        .lineSpacing(1.5)
                        .foregroundColor(Color.secondary)

                    // Skills Chips
                    HStack(spacing: 8) {
                        SkillChip(text: "Kotlin")
                        SkillChip(text: "Compose")
                        SkillChip(text: "MVVM")
                    }
                }
                .padding(20)
                .background(Color.white)
                .cornerRadius(12)
                .shadow(radius: 4)

                // Contact Section
                Text("Entre em Contato")
                    .font(.system(size: 24, weight: .bold))
                    .foregroundColor(Color.black)
                    .padding(.top, 8)

                // Email Card
                ContactCard(icon: "📧", title: "Email", subtitle: "nicolaugalves@gmail.com") {}

                // GitHub Card
                ContactCard(icon: "💻", title: "GitHub", subtitle: "github.com/nicconicco") {}

                Spacer().frame(height: 16)

                // Call to Action Button
                Button(action: {
                    // Action for sending email
                }) {
                    Text("Enviar Email Agora")
                        .font(.system(size: 16, weight: .bold))
                        .frame(maxWidth: .infinity, minHeight: 56)
                        .background(Color.blue)
                        .foregroundColor(.white)
                        .cornerRadius(12)
                }

                Spacer().frame(height: 16)
            }
            .padding(16)
            .background(LinearGradient(gradient: Gradient(colors: [Color.blue.opacity(0.3), Color.white]), startPoint: .top, endPoint: .bottom))
            .edgesIgnoringSafeArea(.all)
        }
    }
}

struct ContactCard: View {
    let icon: String
    let title: String
    let subtitle: String
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            HStack(spacing: 16) {
                Circle()
                    .fill(Color.blue.opacity(0.2))
                    .frame(width: 56, height: 56)
                    .overlay(Text(icon).font(.system(size: 28)))

                VStack(alignment: .leading) {
                    Text(title)
                        .font(.system(size: 18, weight: .bold))
                        .foregroundColor(Color.black)

                    Text(subtitle)
                        .font(.system(size: 14))
                        .foregroundColor(Color.secondary)
                }

                Spacer()

                Text("→")
                    .font(.system(size: 24))
                    .foregroundColor(Color.blue)
                    .fontWeight(.bold)
            }
            .padding(20)
            .background(Color.white)
            .cornerRadius(12)
            .shadow(radius: 4)
        }
    }
}

struct SkillChip: View {
    let text: String

    var body: some View {
        Text(text)
            .font(.system(size: 13, weight: .medium))
            .padding(.horizontal, 12)
            .padding(.vertical, 6)
            .background(Color.blue.opacity(0.2))
            .cornerRadius(16)
    }
}

struct HireMeView_Previews: PreviewProvider {
    static var previews: some View {
        HireMeView()
    }
}
