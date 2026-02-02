//
//  CategoryChipsView.swift
//  iosApp
//
//  Created by Carlos Nicolau Galves
//

import SwiftUI
import ComposeApp

struct CategoryChipsView: View {
    let categories: [ProductCategory]
    let selectedCategory: ProductCategory?
    let onCategorySelected: (ProductCategory?) -> Void

    var body: some View {
        ScrollView(.horizontal, showsIndicators: false) {
            HStack(spacing: 8) {
                ChipButton(
                    title: "Todos",
                    isSelected: selectedCategory == nil,
                    action: { onCategorySelected(nil) }
                )

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
        .background(Color(.systemBackground))
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
