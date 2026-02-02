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
        NavigationView {
            VStack(spacing: 0) {
                // Category chips
                if !viewModel.categories.isEmpty {
                    CategoryChipsView(
                        categories: viewModel.categories,
                        selectedCategory: viewModel.selectedCategory,
                        onCategorySelected: { category in
                            viewModel.selectCategory(category)
                        }
                    )
                }

                // Products grid
                if viewModel.isProductsLoading {
                    Spacer()
                    ProgressView()
                        .progressViewStyle(CircularProgressViewStyle())
                    Spacer()
                } else if viewModel.products.isEmpty {
                    Spacer()
                    Text("Nenhum produto encontrado")
                        .foregroundColor(.secondary)
                    Spacer()
                } else {
                    ProductsGridView(
                        products: viewModel.products,
                        onProductClick: onProductClick,
                        onAddToCart: { product in
                            viewModel.addToCart(product: product)
                        }
                    )
                }
            }
            .navigationTitle("Loja")
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button(action: onCartClick) {
                        ZStack(alignment: .topTrailing) {
                            Image(systemName: "cart")
                                .font(.title2)
                            if viewModel.cart.totalItems > 0 {
                                Text("\(viewModel.cart.totalItems)")
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
            }
        }
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

// MARK: - Category Chips View
struct CategoryChipsView: View {
    let categories: [ProductCategory]
    let selectedCategory: ProductCategory?
    let onCategorySelected: (ProductCategory?) -> Void

    var body: some View {
        ScrollView(.horizontal, showsIndicators: false) {
            HStack(spacing: 8) {
                // "Todos" chip
                ChipButton(
                    title: "Todos",
                    isSelected: selectedCategory == nil,
                    action: { onCategorySelected(nil) }
                )

                // Category chips
                ForEach(categories, id: \.id) { category in
                    ChipButton(
                        title: category.nome,
                        isSelected: selectedCategory?.id == category.id,
                        action: { onCategorySelected(category) }
                    )
                }
            }
            .padding(.horizontal, 16)
            .padding(.vertical, 8)
        }
    }
}

// MARK: - Chip Button
struct ChipButton: View {
    let title: String
    let isSelected: Bool
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            Text(title)
                .font(.subheadline)
                .fontWeight(isSelected ? .semibold : .regular)
                .padding(.horizontal, 16)
                .padding(.vertical, 8)
                .background(isSelected ? Color.accentColor : Color(.systemGray6))
                .foregroundColor(isSelected ? .white : .primary)
                .clipShape(Capsule())
        }
    }
}

// MARK: - Products Grid View
struct ProductsGridView: View {
    let products: [Product]
    let onProductClick: (Product) -> Void
    let onAddToCart: (Product) -> Void

    let columns = [
        GridItem(.flexible(), spacing: 12),
        GridItem(.flexible(), spacing: 12)
    ]

    var body: some View {
        ScrollView {
            LazyVGrid(columns: columns, spacing: 12) {
                ForEach(products, id: \.id) { product in
                    ProductCardView(
                        product: product,
                        onProductClick: onProductClick,
                        onAddToCart: onAddToCart
                    )
                }
            }
            .padding(16)
        }
    }
}

// MARK: - Product Card View
struct ProductCardView: View {
    let product: Product
    let onProductClick: (Product) -> Void
    let onAddToCart: (Product) -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            // Product image placeholder
            ZStack {
                RoundedRectangle(cornerRadius: 12)
                    .fill(Color(.systemGray5))
                    .aspectRatio(1, contentMode: .fit)

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
                                .font(.largeTitle)
                                .foregroundColor(.gray)
                        @unknown default:
                            Image(systemName: "photo")
                                .font(.largeTitle)
                                .foregroundColor(.gray)
                        }
                    }
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
                    .clipShape(RoundedRectangle(cornerRadius: 12))
                } else {
                    Image(systemName: "photo")
                        .font(.largeTitle)
                        .foregroundColor(.gray)
                }
            }
            .onTapGesture {
                onProductClick(product)
            }

            // Product info
            VStack(alignment: .leading, spacing: 4) {
                Text(product.nome)
                    .font(.subheadline)
                    .fontWeight(.semibold)
                    .lineLimit(2)

                Text(product.formattedPrice)
                    .font(.headline)
                    .foregroundColor(.accentColor)
            }

            // Add to cart button
            Button(action: { onAddToCart(product) }) {
                HStack {
                    Image(systemName: "cart.badge.plus")
                    Text("Adicionar")
                        .font(.caption)
                        .fontWeight(.semibold)
                }
                .frame(maxWidth: .infinity)
                .padding(.vertical, 8)
                .background(Color.accentColor)
                .foregroundColor(.white)
                .clipShape(RoundedRectangle(cornerRadius: 8))
            }
        }
        .padding(8)
        .background(Color(.systemBackground))
        .clipShape(RoundedRectangle(cornerRadius: 12))
        .shadow(color: Color.black.opacity(0.1), radius: 4, x: 0, y: 2)
    }
}

// MARK: - Product Detail View
struct ProductDetailView: View {
    let product: Product
    let onBackClick: () -> Void
    let onAddToCart: (Product, Int32) -> Void

    @State private var quantity: Int32 = 1

    var body: some View {
        NavigationView {
            ScrollView {
                VStack(alignment: .leading, spacing: 16) {
                    // Product image
                    ZStack {
                        RoundedRectangle(cornerRadius: 16)
                            .fill(Color(.systemGray5))
                            .aspectRatio(1, contentMode: .fit)

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
                                        .font(.system(size: 60))
                                        .foregroundColor(.gray)
                                @unknown default:
                                    Image(systemName: "photo")
                                        .font(.system(size: 60))
                                        .foregroundColor(.gray)
                                }
                            }
                            .frame(maxWidth: .infinity, maxHeight: 300)
                            .clipShape(RoundedRectangle(cornerRadius: 16))
                        } else {
                            Image(systemName: "photo")
                                .font(.system(size: 60))
                                .foregroundColor(.gray)
                        }
                    }
                    .frame(height: 300)

                    // Product info
                    VStack(alignment: .leading, spacing: 12) {
                        Text(product.nome)
                            .font(.title2)
                            .fontWeight(.bold)

                        Text(product.formattedPrice)
                            .font(.title)
                            .foregroundColor(.accentColor)

                        if !product.descricao.isEmpty {
                            Text(product.descricao)
                                .font(.body)
                                .foregroundColor(.secondary)
                        }
                    }

                    Spacer(minLength: 20)

                    // Quantity selector
                    HStack {
                        Text("Quantidade:")
                            .font(.headline)

                        Spacer()

                        HStack(spacing: 16) {
                            Button(action: {
                                if quantity > 1 { quantity -= 1 }
                            }) {
                                Image(systemName: "minus.circle.fill")
                                    .font(.title2)
                                    .foregroundColor(quantity > 1 ? .accentColor : .gray)
                            }
                            .disabled(quantity <= 1)

                            Text("\(quantity)")
                                .font(.title3)
                                .fontWeight(.semibold)
                                .frame(minWidth: 40)

                            Button(action: { quantity += 1 }) {
                                Image(systemName: "plus.circle.fill")
                                    .font(.title2)
                                    .foregroundColor(.accentColor)
                            }
                        }
                    }
                    .padding()
                    .background(Color(.systemGray6))
                    .clipShape(RoundedRectangle(cornerRadius: 12))

                    // Add to cart button
                    Button(action: {
                        onAddToCart(product, quantity)
                    }) {
                        HStack {
                            Image(systemName: "cart.badge.plus")
                            Text("Adicionar ao Carrinho")
                                .fontWeight(.semibold)
                        }
                        .frame(maxWidth: .infinity)
                        .padding()
                        .background(Color.accentColor)
                        .foregroundColor(.white)
                        .clipShape(RoundedRectangle(cornerRadius: 12))
                    }
                }
                .padding()
            }
            .navigationTitle("Detalhes")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    Button(action: onBackClick) {
                        Image(systemName: "chevron.left")
                    }
                }
            }
        }
    }
}

// MARK: - Cart View
struct CartView: View {
    @Bindable var viewModel: StoreViewModel
    let onBackClick: () -> Void
    let onCheckout: () -> Void

    var body: some View {
        NavigationView {
            VStack {
                if viewModel.cart.items.isEmpty {
                    Spacer()
                    VStack(spacing: 16) {
                        Image(systemName: "cart")
                            .font(.system(size: 60))
                            .foregroundColor(.gray)
                        Text("Seu carrinho está vazio")
                            .font(.title3)
                            .foregroundColor(.secondary)
                    }
                    Spacer()
                } else {
                    List {
                        ForEach(viewModel.cart.items, id: \.id) { item in
                            CartItemRow(
                                item: item,
                                onUpdateQuantity: { newQuantity in
                                    viewModel.updateQuantity(itemId: item.id, quantidade: newQuantity)
                                },
                                onRemove: {
                                    viewModel.removeFromCart(itemId: item.id)
                                }
                            )
                        }
                    }
                    .listStyle(.plain)

                    // Total and checkout
                    VStack(spacing: 16) {
                        Divider()

                        HStack {
                            Text("Total:")
                                .font(.headline)
                            Spacer()
                            Text(String(format: "R$ %.2f", viewModel.cart.totalPrice))
                                .font(.title2)
                                .fontWeight(.bold)
                                .foregroundColor(.accentColor)
                        }
                        .padding(.horizontal)

                        Button(action: onCheckout) {
                            if viewModel.isCheckingOut {
                                ProgressView()
                                    .progressViewStyle(CircularProgressViewStyle(tint: .white))
                            } else {
                                Text("Finalizar Pedido")
                                    .fontWeight(.semibold)
                            }
                        }
                        .frame(maxWidth: .infinity)
                        .padding()
                        .background(Color.accentColor)
                        .foregroundColor(.white)
                        .clipShape(RoundedRectangle(cornerRadius: 12))
                        .disabled(viewModel.isCheckingOut)
                        .padding(.horizontal)
                        .padding(.bottom)
                    }
                }
            }
            .navigationTitle("Carrinho")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    Button(action: onBackClick) {
                        Image(systemName: "chevron.left")
                    }
                }
            }
        }
    }
}

// MARK: - Cart Item Row
struct CartItemRow: View {
    let item: CartItem
    let onUpdateQuantity: (Int32) -> Void
    let onRemove: () -> Void

    var body: some View {
        HStack(spacing: 12) {
            // Product image
            ZStack {
                RoundedRectangle(cornerRadius: 8)
                    .fill(Color(.systemGray5))
                    .frame(width: 60, height: 60)

                if !item.product.imagem.isEmpty {
                    AsyncImage(url: URL(string: item.product.imagem)) { phase in
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

            // Product info
            VStack(alignment: .leading, spacing: 4) {
                Text(item.product.nome)
                    .font(.subheadline)
                    .fontWeight(.semibold)
                    .lineLimit(2)

                Text(item.product.formattedPrice)
                    .font(.caption)
                    .foregroundColor(.accentColor)
            }

            Spacer()

            // Quantity controls
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
                        .foregroundColor(.accentColor)
                }

                Text("\(item.quantidade)")
                    .font(.subheadline)
                    .fontWeight(.semibold)
                    .frame(minWidth: 24)

                Button(action: {
                    onUpdateQuantity(item.quantidade + 1)
                }) {
                    Image(systemName: "plus.circle")
                        .foregroundColor(.accentColor)
                }
            }

            // Remove button
            Button(action: onRemove) {
                Image(systemName: "trash")
                    .foregroundColor(.red)
            }
        }
        .padding(.vertical, 8)
    }
}

// MARK: - More View (replaces individual tabs)
struct MoreView: View {
    var onProfileClick: () -> Void
    var onAlarmsClick: () -> Void
    var onContactClick: () -> Void

    var body: some View {
        NavigationView {
            List {
                Button(action: onProfileClick) {
                    HStack {
                        Image(systemName: "person.circle")
                            .foregroundColor(.accentColor)
                        Text("Perfil")
                    }
                }

                Button(action: onAlarmsClick) {
                    HStack {
                        Image(systemName: "bell")
                            .foregroundColor(.accentColor)
                        Text("Notificações")
                    }
                }

                Button(action: onContactClick) {
                    HStack {
                        Image(systemName: "briefcase")
                            .foregroundColor(.accentColor)
                        Text("Contrate")
                    }
                }
            }
            .navigationTitle("Mais")
        }
    }
}
