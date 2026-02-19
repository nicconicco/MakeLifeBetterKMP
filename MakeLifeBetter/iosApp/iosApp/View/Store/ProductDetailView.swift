//
//  ProductDetailView.swift
//  iosApp
//
//  Created by Carlos Nicolau Galves
//

import SwiftUI
import ComposeApp

struct ProductDetailView: View {
    let product: Product
    let onBackClick: () -> Void
    let onAddToCart: (Product, Int32) -> Void
    var allProducts: [Product] = []
    var onProductClick: (Product) -> Void = { _ in }

    @State private var quantity: Int32 = 1

    private var suggestedProducts: [Product] {
        allProducts.filter { $0.id != product.id }
    }

    var body: some View {
        ZStack(alignment: .bottom) {
            ScrollViewReader { proxy in
                ScrollView {
                    VStack(spacing: 0) {
                        ProductImageSection(
                            imageUrl: product.imagem,
                            onBackClick: onBackClick
                        )
                        .id("top")

                        ProductInfoCard(
                            name: product.nome,
                            price: product.formattedPrice,
                            description: product.descricao,
                            caracteristicas: product.caracteristicas,
                            curiosidades: product.curiosidades,
                            harmonizacao: product.harmonizacao,
                            quantity: $quantity
                        )

                        if !suggestedProducts.isEmpty {
                            SuggestionsSection(
                                products: suggestedProducts,
                                onProductClick: onProductClick
                            )
                        }
                    }
                    .padding(.bottom, 100)
                }
                .onChange(of: product.id) { _, _ in
                    quantity = 1
                    withAnimation {
                        proxy.scrollTo("top", anchor: .top)
                    }
                }
            }
            .ignoresSafeArea(edges: .top)

            BottomActionBar(
                unitPrice: product.preco,
                quantity: quantity,
                onAddToCart: {
                    onAddToCart(product, quantity)
                }
            )
        }
        .navigationBarHidden(true)
        .background(Color(.systemGroupedBackground))
    }
}

// MARK: - Product Image Section
private struct ProductImageSection: View {
    let imageUrl: String
    let onBackClick: () -> Void

    var body: some View {
        ZStack(alignment: .topLeading) {
            GeometryReader { geometry in
                ZStack {
                    Color(.systemGray5)

                    if !imageUrl.isEmpty {
                        AsyncImage(url: URL(string: imageUrl)) { phase in
                            switch phase {
                            case .empty:
                                ProgressView()
                                    .scaleEffect(1.5)
                            case .success(let image):
                                image
                                    .resizable()
                                    .aspectRatio(contentMode: .fit)
                            case .failure:
                                ImagePlaceholder()
                            @unknown default:
                                ImagePlaceholder()
                            }
                        }
                    } else {
                        ImagePlaceholder()
                    }
                }
                .frame(width: geometry.size.width, height: 350)
            }
            .frame(height: 350)

            SafeAreaBackButton(action: onBackClick)
        }
    }
}

// MARK: - Safe Area Back Button
private struct SafeAreaBackButton: View {
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            Image(systemName: "chevron.left")
                .font(.system(size: 18, weight: .semibold))
                .foregroundColor(.primary)
                .frame(width: 40, height: 40)
                .background(.ultraThinMaterial)
                .clipShape(Circle())
        }
        .padding(.top, 50)
        .padding(.leading, 16)
    }
}

// MARK: - Image Placeholder
private struct ImagePlaceholder: View {
    var body: some View {
        VStack(spacing: 8) {
            Image(systemName: "photo")
                .font(.system(size: 50))
                .foregroundColor(.gray)
            Text("Sem imagem")
                .font(.caption)
                .foregroundColor(.gray)
        }
    }
}

// MARK: - Product Info Card
private struct ProductInfoCard: View {
    let name: String
    let price: String
    let description: String
    let caracteristicas: String
    let curiosidades: String
    let harmonizacao: String
    @Binding var quantity: Int32

    var body: some View {
        VStack(alignment: .leading, spacing: 20) {
            VStack(alignment: .leading, spacing: 8) {
                Text(name)
                    .font(.title2)
                    .fontWeight(.bold)
                    .foregroundColor(.primary)

                Text(price)
                    .font(.title)
                    .fontWeight(.bold)
                    .foregroundColor(.accentColor)
            }

            if !description.isEmpty {
                Divider()

                VStack(alignment: .leading, spacing: 8) {
                    Text("Descrição")
                        .font(.headline)
                        .foregroundColor(.primary)

                    Text(description)
                        .font(.body)
                        .foregroundColor(.secondary)
                        .lineSpacing(4)
                }
            }

            if !caracteristicas.isEmpty {
                Divider()
                DetailSection(
                    icon: "list.bullet.rectangle",
                    title: "Características",
                    content: caracteristicas
                )
            }

            if !curiosidades.isEmpty {
                Divider()
                DetailSection(
                    icon: "lightbulb",
                    title: "Curiosidades",
                    content: curiosidades
                )
            }

            if !harmonizacao.isEmpty {
                Divider()
                DetailSection(
                    icon: "fork.knife",
                    title: "Harmonização",
                    content: harmonizacao
                )
            }

            Divider()

            QuantitySelector(quantity: $quantity)
        }
        .padding(20)
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(Color(.systemBackground))
        .clipShape(RoundedRectangle(cornerRadius: 24, style: .continuous))
        .offset(y: -24)
    }
}

// MARK: - Detail Section
private struct DetailSection: View {
    let icon: String
    let title: String
    let content: String

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            HStack(spacing: 6) {
                Image(systemName: icon)
                    .font(.system(size: 14, weight: .semibold))
                    .foregroundColor(.accentColor)
                Text(title)
                    .font(.headline)
                    .foregroundColor(.primary)
            }

            Text(content)
                .font(.body)
                .foregroundColor(.secondary)
                .lineSpacing(4)
        }
    }
}

// MARK: - Quantity Selector
private struct QuantitySelector: View {
    @Binding var quantity: Int32

    var body: some View {
        HStack {
            Text("Quantidade")
                .font(.headline)

            Spacer()

            HStack(spacing: 0) {
                QuantityButton(
                    icon: "minus",
                    isEnabled: quantity > 1,
                    action: { if quantity > 1 { quantity -= 1 } }
                )

                Text("\(quantity)")
                    .font(.title3)
                    .fontWeight(.semibold)
                    .frame(width: 50)

                QuantityButton(
                    icon: "plus",
                    isEnabled: true,
                    action: { quantity += 1 }
                )
            }
            .background(Color(.systemGray6))
            .clipShape(RoundedRectangle(cornerRadius: 12))
        }
    }
}

// MARK: - Quantity Button
private struct QuantityButton: View {
    let icon: String
    let isEnabled: Bool
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            Image(systemName: icon)
                .font(.system(size: 14, weight: .bold))
                .foregroundColor(isEnabled ? .accentColor : .gray)
                .frame(width: 44, height: 44)
        }
        .disabled(!isEnabled)
    }
}

// MARK: - Suggestions Section
private struct SuggestionsSection: View {
    let products: [Product]
    let onProductClick: (Product) -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            Divider()
                .padding(.horizontal, 20)

            HStack(spacing: 6) {
                Image(systemName: "lightbulb")
                    .font(.system(size: 14, weight: .semibold))
                    .foregroundColor(.accentColor)
                Text("Você também pode gostar")
                    .font(.headline)
                    .foregroundColor(.primary)
            }
            .padding(.horizontal, 20)

            ScrollView(.horizontal, showsIndicators: false) {
                HStack(spacing: 12) {
                    ForEach(products, id: \.id) { product in
                        SuggestionCard(
                            product: product,
                            onClick: { onProductClick(product) }
                        )
                    }
                }
                .padding(.horizontal, 20)
            }
        }
        .padding(.top, 8)
    }
}

// MARK: - Suggestion Card
private struct SuggestionCard: View {
    let product: Product
    let onClick: () -> Void

    var body: some View {
        Button(action: onClick) {
            VStack(alignment: .leading, spacing: 0) {
                ZStack {
                    Color(.systemGray5)

                    if !product.imagem.isEmpty {
                        AsyncImage(url: URL(string: product.imagem)) { phase in
                            switch phase {
                            case .empty:
                                ProgressView()
                            case .success(let image):
                                image
                                    .resizable()
                                    .aspectRatio(contentMode: .fill)
                            case .failure:
                                Image(systemName: "photo")
                                    .font(.system(size: 24))
                                    .foregroundColor(.gray)
                            @unknown default:
                                Image(systemName: "photo")
                                    .font(.system(size: 24))
                                    .foregroundColor(.gray)
                            }
                        }
                    } else {
                        Image(systemName: "photo")
                            .font(.system(size: 24))
                            .foregroundColor(.gray)
                    }
                }
                .frame(width: 160, height: 160)
                .clipped()

                VStack(alignment: .leading, spacing: 4) {
                    Text(product.nome)
                        .font(.subheadline)
                        .fontWeight(.medium)
                        .foregroundColor(.primary)
                        .lineLimit(1)

                    Text(product.formattedPrice)
                        .font(.subheadline)
                        .fontWeight(.bold)
                        .foregroundColor(.accentColor)
                }
                .padding(8)
            }
            .frame(width: 160)
            .background(Color(.systemBackground))
            .clipShape(RoundedRectangle(cornerRadius: 12, style: .continuous))
            .shadow(color: .black.opacity(0.08), radius: 4, x: 0, y: 2)
        }
        .buttonStyle(.plain)
    }
}

// MARK: - Bottom Action Bar
private struct BottomActionBar: View {
    let unitPrice: Double
    let quantity: Int32
    let onAddToCart: () -> Void

    private var totalPrice: Double {
        unitPrice * Double(quantity)
    }

    private var formattedTotal: String {
        String(format: "R$ %.2f", totalPrice)
    }

    var body: some View {
        VStack(spacing: 0) {
            Divider()

            HStack(spacing: 16) {
                VStack(alignment: .leading, spacing: 2) {
                    Text("Total")
                        .font(.caption)
                        .foregroundColor(.secondary)
                    Text(formattedTotal)
                        .font(.title3)
                        .fontWeight(.bold)
                        .foregroundColor(.primary)
                }

                Spacer()

                Button(action: onAddToCart) {
                    HStack(spacing: 8) {
                        Image(systemName: "cart.badge.plus")
                            .font(.system(size: 16, weight: .semibold))
                        Text("Adicionar")
                            .font(.headline)
                    }
                    .foregroundColor(.white)
                    .padding(.horizontal, 24)
                    .padding(.vertical, 14)
                    .background(Color.accentColor)
                    .clipShape(RoundedRectangle(cornerRadius: 14))
                }
            }
            .padding(.horizontal, 20)
            .padding(.vertical, 12)
            .background(.regularMaterial)
        }
    }
}
