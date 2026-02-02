//
//  ProductsGridView.swift
//  iosApp
//
//  Created by Carlos Nicolau Galves
//

import SwiftUI
import ComposeApp

struct ProductsGridView: View {
    let products: [Product]
    let onProductClick: (Product) -> Void
    let onAddToCart: (Product) -> Void

    private let columns = [
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
            .padding(.horizontal, 12)
            .padding(.vertical, 12)
        }
    }
}
