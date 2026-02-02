//
//  CartView.swift
//  iosApp
//
//  Created by Carlos Nicolau Galves
//

import SwiftUI
import ComposeApp

struct CartView: View {
    @Bindable var viewModel: StoreViewModel
    let onBackClick: () -> Void
    let onCheckout: () -> Void

    var body: some View {
        VStack(spacing: 0) {
            CartHeaderView(onBackClick: onBackClick)
                .background(Color(.systemBackground))

            Divider()

            if viewModel.cart.items.isEmpty {
                EmptyCartView()
            } else {
                ScrollView {
                    LazyVStack(spacing: 0) {
                        ForEach(viewModel.cart.items, id: \.id) { item in
                            CartItemRowView(
                                item: item,
                                onUpdateQuantity: { newQuantity in
                                    viewModel.updateQuantity(itemId: item.id, quantidade: newQuantity)
                                },
                                onRemove: {
                                    viewModel.removeFromCart(itemId: item.id)
                                }
                            )
                            Divider()
                                .padding(.leading, 80)
                        }
                    }
                }

                CartFooterView(
                    totalPrice: viewModel.cart.totalPrice,
                    isCheckingOut: viewModel.isCheckingOut,
                    onCheckout: onCheckout
                )
            }
        }
        .background(Color(.systemBackground))
        .navigationBarHidden(true)
    }
}

// MARK: - Cart Header View
private struct CartHeaderView: View {
    let onBackClick: () -> Void

    var body: some View {
        ZStack {
            Text("Carrinho")
                .font(.headline)
                .fontWeight(.semibold)

            HStack {
                Button(action: onBackClick) {
                    HStack(spacing: 4) {
                        Image(systemName: "chevron.left")
                            .font(.system(size: 16, weight: .medium))
                        Text("Voltar")
                            .font(.body)
                    }
                    .foregroundColor(.accentColor)
                }

                Spacer()
            }
        }
        .padding(.horizontal, 16)
        .padding(.vertical, 12)
    }
}

// MARK: - Empty Cart View
private struct EmptyCartView: View {
    var body: some View {
        VStack(spacing: 16) {
            Spacer()
            Image(systemName: "cart")
                .font(.system(size: 60))
                .foregroundColor(.gray)
            Text("Seu carrinho está vazio")
                .font(.title3)
                .foregroundColor(.secondary)
            Spacer()
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
    }
}

// MARK: - Cart Item Row View
private struct CartItemRowView: View {
    let item: CartItem
    let onUpdateQuantity: (Int32) -> Void
    let onRemove: () -> Void

    var body: some View {
        HStack(spacing: 12) {
            AsyncImage(url: URL(string: item.product.imagem)) { phase in
                switch phase {
                case .success(let image):
                    image
                        .resizable()
                        .aspectRatio(contentMode: .fill)
                default:
                    Color(.systemGray5)
                        .overlay(
                            Image(systemName: "photo")
                                .foregroundColor(.gray)
                        )
                }
            }
            .frame(width: 60, height: 60)
            .clipShape(RoundedRectangle(cornerRadius: 8))

            VStack(alignment: .leading, spacing: 4) {
                Text(item.product.nome)
                    .font(.subheadline)
                    .fontWeight(.medium)
                    .lineLimit(2)

                Text(item.product.formattedPrice)
                    .font(.subheadline)
                    .foregroundColor(.accentColor)
                    .fontWeight(.semibold)
            }
            .frame(maxWidth: .infinity, alignment: .leading)

            HStack(spacing: 8) {
                Button(action: {
                    let newQty = item.quantidade - 1
                    if newQty > 0 {
                        onUpdateQuantity(newQty)
                    } else {
                        onRemove()
                    }
                }) {
                    Image(systemName: "minus.circle")
                        .font(.title3)
                        .foregroundColor(.accentColor)
                }

                Text("\(item.quantidade)")
                    .font(.subheadline)
                    .fontWeight(.semibold)
                    .frame(minWidth: 20)

                Button(action: {
                    onUpdateQuantity(item.quantidade + 1)
                }) {
                    Image(systemName: "plus.circle")
                        .font(.title3)
                        .foregroundColor(.accentColor)
                }
            }

            Button(action: onRemove) {
                Image(systemName: "trash")
                    .font(.subheadline)
                    .foregroundColor(.red)
            }
            .padding(.leading, 4)
        }
        .padding(.horizontal, 16)
        .padding(.vertical, 12)
    }
}

// MARK: - Cart Footer View
private struct CartFooterView: View {
    let totalPrice: Double
    let isCheckingOut: Bool
    let onCheckout: () -> Void

    var body: some View {
        VStack(spacing: 12) {
            Divider()

            HStack {
                Text("Total:")
                    .font(.headline)
                Spacer()
                Text(String(format: "R$ %.2f", totalPrice))
                    .font(.title2)
                    .fontWeight(.bold)
                    .foregroundColor(.accentColor)
            }
            .padding(.horizontal, 16)

            Button(action: onCheckout) {
                if isCheckingOut {
                    ProgressView()
                        .progressViewStyle(CircularProgressViewStyle(tint: .white))
                } else {
                    Text("Finalizar Pedido")
                        .fontWeight(.semibold)
                }
            }
            .frame(maxWidth: .infinity)
            .padding(.vertical, 14)
            .background(Color.accentColor)
            .foregroundColor(.white)
            .clipShape(RoundedRectangle(cornerRadius: 12))
            .disabled(isCheckingOut)
            .padding(.horizontal, 16)
            .padding(.bottom, 8)
        }
        .background(Color(.systemBackground))
    }
}
