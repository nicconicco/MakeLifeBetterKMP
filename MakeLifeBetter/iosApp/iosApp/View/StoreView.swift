//
//  StoreView.swift
//  iosApp
//
//  Created by Carlos Nicolau Galves
//

import SwiftUI
import ComposeApp

struct StoreView: View {
    @Bindable var viewModel: StoreViewModel
    var onProductClick: (Product) -> Void
    var onCartClick: () -> Void

    var body: some View {
        VStack(spacing: 0) {
            StoreHeaderView(
                cartItemCount: viewModel.cart.totalItems,
                onCartClick: onCartClick
            )

            if !viewModel.categories.isEmpty {
                CategoryChipsView(
                    categories: viewModel.categories,
                    selectedCategory: viewModel.selectedCategory,
                    onCategorySelected: { category in
                        viewModel.selectCategory(category)
                    }
                )
            }

            StoreContentView(
                isLoading: viewModel.isProductsLoading,
                products: viewModel.products,
                onProductClick: onProductClick,
                onAddToCart: { product in
                    viewModel.addToCart(product: product)
                }
            )
        }
        .navigationBarHidden(true)
        .alert("Erro ao carregar", isPresented: .constant(viewModel.errorMessage != nil)) {
            Button("Tentar novamente") {
                viewModel.clearError()
                viewModel.loadProducts()
                viewModel.loadCategories()
            }
        } message: {
            Text(viewModel.errorMessage ?? "Erro desconhecido")
        }
    }
}

// MARK: - Store Header View
private struct StoreHeaderView: View {
    let cartItemCount: Int32
    let onCartClick: () -> Void

    var body: some View {
        HStack {
            Text("Loja")
                .font(.largeTitle)
                .fontWeight(.bold)

            Spacer()

            Button(action: onCartClick) {
                ZStack(alignment: .topTrailing) {
                    Image(systemName: "cart")
                        .font(.title2)
                        .foregroundColor(.accentColor)

                    if cartItemCount > 0 {
                        Text("\(cartItemCount)")
                            .font(.caption2)
                            .fontWeight(.bold)
                            .foregroundColor(.white)
                            .padding(4)
                            .background(Color.red)
                            .clipShape(Circle())
                            .offset(x: 8, y: -8)
                    }
                }
            }
        }
        .padding(.horizontal, 16)
        .padding(.top, 8)
        .padding(.bottom, 8)
        .background(Color(.systemBackground))
    }
}

// MARK: - Store Content View
private struct StoreContentView: View {
    let isLoading: Bool
    let products: [Product]
    let onProductClick: (Product) -> Void
    let onAddToCart: (Product) -> Void

    var body: some View {
        if isLoading {
            LoadingView()
        } else if products.isEmpty {
            EmptyProductsView()
        } else {
            ProductsGridView(
                products: products,
                onProductClick: onProductClick,
                onAddToCart: onAddToCart
            )
        }
    }
}

// MARK: - Loading View
private struct LoadingView: View {
    var body: some View {
        VStack {
            Spacer()
            ProgressView()
                .progressViewStyle(CircularProgressViewStyle())
                .scaleEffect(1.2)
            Spacer()
        }
    }
}

// MARK: - Empty Products View
private struct EmptyProductsView: View {
    var body: some View {
        VStack(spacing: 16) {
            Spacer()
            Image(systemName: "bag")
                .font(.system(size: 50))
                .foregroundColor(.gray)
            Text("Nenhum produto encontrado")
                .foregroundColor(.secondary)
            Spacer()
        }
    }
}

// MARK: - Order Confirmation View
struct OrderConfirmationView: View {
    let order: Order?
    let onBackToStore: () -> Void

    var body: some View {
        VStack(spacing: 0) {
            ConfirmationHeaderView()

            VStack(spacing: 24) {
                Spacer()

                Image(systemName: "checkmark.circle.fill")
                    .font(.system(size: 80))
                    .foregroundColor(.green)

                Text("Pedido Confirmado!")
                    .font(.title)
                    .fontWeight(.bold)

                if let order = order {
                    VStack(spacing: 8) {
                        Text("Pedido: \(order.id)")
                            .font(.headline)
                            .foregroundColor(.secondary)

                        Text(String(format: "Total: R$ %.2f", order.totalPrice))
                            .font(.title2)
                            .fontWeight(.semibold)
                            .foregroundColor(.accentColor)
                    }
                }

                Text("Obrigado pela sua compra!")
                    .font(.body)
                    .foregroundColor(.secondary)

                Spacer()

                Button(action: onBackToStore) {
                    Text("Voltar para a Loja")
                        .fontWeight(.semibold)
                        .frame(maxWidth: .infinity)
                        .padding()
                        .background(Color.accentColor)
                        .foregroundColor(.white)
                        .clipShape(RoundedRectangle(cornerRadius: 12))
                }
                .padding(.horizontal)
                .padding(.bottom)
            }
        }
        .navigationBarHidden(true)
    }
}

// MARK: - Confirmation Header View
private struct ConfirmationHeaderView: View {
    var body: some View {
        HStack {
            Spacer()
            Text("Confirmação")
                .font(.headline)
                .fontWeight(.semibold)
            Spacer()
        }
        .padding(.horizontal, 16)
        .padding(.vertical, 12)
        .background(Color(.systemBackground))
    }
}

// MARK: - More View
struct MoreView: View {
    var onProfileClick: () -> Void
    var onAlarmsClick: () -> Void
    var onContactClick: () -> Void

    var body: some View {
        List {
            MoreMenuItem(
                icon: "person.circle",
                title: "Perfil",
                action: onProfileClick
            )

            MoreMenuItem(
                icon: "bell",
                title: "Notificações",
                action: onAlarmsClick
            )

            MoreMenuItem(
                icon: "briefcase",
                title: "Contrate",
                action: onContactClick
            )
        }
        .navigationTitle("Mais")
    }
}

// MARK: - More Menu Item
private struct MoreMenuItem: View {
    let icon: String
    let title: String
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            HStack {
                Image(systemName: icon)
                    .foregroundColor(.accentColor)
                Text(title)
            }
        }
    }
}
