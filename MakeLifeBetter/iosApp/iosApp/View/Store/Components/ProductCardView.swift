//
//  ProductCardView.swift
//  iosApp
//
//  Created by Carlos Nicolau Galves
//

import SwiftUI
import ComposeApp

struct ProductCardView: View {
    let product: Product
    let onProductClick: (Product) -> Void
    let onAddToCart: (Product) -> Void

    private let imageHeight: CGFloat = 120
    private let infoHeight: CGFloat = 50
    private let buttonHeight: CGFloat = 36

    var body: some View {
        VStack(spacing: 8) {
            ProductImageView(imageUrl: product.imagem)
                .frame(height: imageHeight)
                .frame(maxWidth: .infinity)
                .clipShape(RoundedRectangle(cornerRadius: 12))
                .onTapGesture {
                    onProductClick(product)
                }

            ProductInfoView(
                name: product.nome,
                price: product.formattedPrice
            )
            .frame(height: infoHeight, alignment: .top)
            .frame(maxWidth: .infinity)

            AddToCartButton {
                onAddToCart(product)
            }
            .frame(height: buttonHeight)
        }
        .padding(8)
        .frame(maxWidth: .infinity)
        .background(Color(.systemBackground))
        .clipShape(RoundedRectangle(cornerRadius: 12))
        .shadow(color: Color.black.opacity(0.1), radius: 4, x: 0, y: 2)
    }
}

// MARK: - Product Image View
private struct ProductImageView: View {
    let imageUrl: String

    var body: some View {
        ZStack {
            RoundedRectangle(cornerRadius: 12)
                .fill(Color(.systemGray5))

            if !imageUrl.isEmpty {
                AsyncImage(url: URL(string: imageUrl)) { phase in
                    switch phase {
                    case .empty:
                        ProgressView()
                    case .success(let image):
                        image
                            .resizable()
                            .aspectRatio(contentMode: .fill)
                    case .failure:
                        PlaceholderImageView()
                    @unknown default:
                        PlaceholderImageView()
                    }
                }
                .frame(maxWidth: .infinity, maxHeight: .infinity)
                .clipped()
            } else {
                PlaceholderImageView()
            }
        }
    }
}

// MARK: - Placeholder Image View
private struct PlaceholderImageView: View {
    var body: some View {
        Image(systemName: "photo")
            .font(.largeTitle)
            .foregroundColor(.gray)
    }
}

// MARK: - Product Info View
private struct ProductInfoView: View {
    let name: String
    let price: String

    var body: some View {
        VStack(alignment: .leading, spacing: 2) {
            Text(name)
                .font(.subheadline)
                .fontWeight(.semibold)
                .lineLimit(2)
                .multilineTextAlignment(.leading)
                .frame(maxWidth: .infinity, alignment: .leading)

            Text(price)
                .font(.headline)
                .foregroundColor(.accentColor)
                .frame(maxWidth: .infinity, alignment: .leading)
        }
    }
}

// MARK: - Add To Cart Button
private struct AddToCartButton: View {
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            HStack(spacing: 4) {
                Image(systemName: "cart.badge.plus")
                    .font(.caption)
                Text("Adicionar")
                    .font(.caption)
                    .fontWeight(.semibold)
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity)
            .background(Color.accentColor)
            .foregroundColor(.white)
            .clipShape(RoundedRectangle(cornerRadius: 8))
        }
    }
}
