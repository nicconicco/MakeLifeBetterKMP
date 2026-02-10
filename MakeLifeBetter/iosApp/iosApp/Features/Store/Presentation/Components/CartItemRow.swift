//
//  CartItemRow.swift
//  iosApp
//
//  Created by Carlos Nicolau Galves
//

import SwiftUI
import ComposeApp

struct CartItemRow: View {
    let item: CartItem
    let onUpdateQuantity: (Int32) -> Void
    let onRemove: () -> Void

    var body: some View {
        HStack(spacing: 12) {
            CartItemImageView(imageUrl: item.product.imagem)

            CartItemInfoView(
                name: item.product.nome,
                price: item.product.formattedPrice
            )

            Spacer()

            QuantityControlView(
                quantity: item.quantidade,
                onDecrease: {
                    let newQty = item.quantidade - 1
                    if newQty > 0 {
                        onUpdateQuantity(newQty)
                    } else {
                        onRemove()
                    }
                },
                onIncrease: {
                    onUpdateQuantity(item.quantidade + 1)
                }
            )

            RemoveButton(action: onRemove)
        }
        .padding(.vertical, 8)
    }
}

// MARK: - Cart Item Image View
private struct CartItemImageView: View {
    let imageUrl: String

    var body: some View {
        ZStack {
            RoundedRectangle(cornerRadius: 8)
                .fill(Color(.systemGray5))
                .frame(width: 60, height: 60)

            if !imageUrl.isEmpty {
                AsyncImage(url: URL(string: imageUrl)) { phase in
                    switch phase {
                    case .success(let image):
                        image
                            .resizable()
                            .aspectRatio(contentMode: .fill)
                    default:
                        Image(systemName: "photo")
                            .foregroundColor(.gray)
                    }
                }
                .frame(width: 60, height: 60)
                .clipShape(RoundedRectangle(cornerRadius: 8))
            } else {
                Image(systemName: "photo")
                    .foregroundColor(.gray)
            }
        }
    }
}

// MARK: - Cart Item Info View
private struct CartItemInfoView: View {
    let name: String
    let price: String

    var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            Text(name)
                .font(.subheadline)
                .fontWeight(.semibold)
                .lineLimit(2)

            Text(price)
                .font(.caption)
                .foregroundColor(.accentColor)
        }
    }
}

// MARK: - Quantity Control View
private struct QuantityControlView: View {
    let quantity: Int32
    let onDecrease: () -> Void
    let onIncrease: () -> Void

    var body: some View {
        HStack(spacing: 8) {
            Button(action: onDecrease) {
                Image(systemName: "minus.circle")
                    .foregroundColor(.accentColor)
            }

            Text("\(quantity)")
                .font(.subheadline)
                .fontWeight(.semibold)
                .frame(minWidth: 24)

            Button(action: onIncrease) {
                Image(systemName: "plus.circle")
                    .foregroundColor(.accentColor)
            }
        }
    }
}

// MARK: - Remove Button
private struct RemoveButton: View {
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            Image(systemName: "trash")
                .foregroundColor(.red)
        }
    }
}
