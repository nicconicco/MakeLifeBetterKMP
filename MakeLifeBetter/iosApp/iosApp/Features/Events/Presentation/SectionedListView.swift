//
//  SectionedListScreen.swift
//  iosApp
//
//  Created by Carlos Nicolau Galves on 22/01/26.
//


import SwiftUI

struct SectionedListView: View {
    var viewModel: EventViewModel
    var onItemClick: (Event) -> Void = { _ in }

    var body: some View {
        if viewModel.isLoading {
            ProgressView()
                .scaleEffect(1.5, anchor: .center)
                .frame(maxWidth: .infinity, maxHeight: .infinity)
        } else if viewModel.sections.isEmpty {
            VStack(spacing: 12) {
                Text("Nenhum evento encontrado")
                    .font(.headline)
                    .foregroundColor(.secondary)
                Button("Tentar novamente") {
                    viewModel.refreshSections()
                }
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity)
        } else {
            List {
                ForEach(viewModel.sections, id: \.titulo) { section in
                    Section(header: SectionHeader(title: section.titulo)) {
                        ForEach(section.eventos, id: \.id) { event in
                            EventCard(event: event, onClick: { onItemClick(event) })
                                .listRowSeparator(.hidden)
                                .listRowInsets(EdgeInsets(top: 4, leading: 16, bottom: 4, trailing: 16))
                        }
                    }
                }
            }
            .listStyle(.plain)
            .refreshable {
                viewModel.refreshSections()
            }
        }
    }
}

// Section header view
struct SectionHeader: View {
    var title: String

    var body: some View {
        Text(title)
            .font(.headline)
            .fontWeight(.bold)
            .foregroundColor(.blue) // Adjust based on your color scheme
            .padding(.top, 16)
            .padding(.bottom, 12)
    }
}

// Event card view
struct EventCard: View {
    var event: Event
    var onClick: () -> Void

    var body: some View {
        Button(action: onClick) {
            VStack(alignment: .leading, spacing: 4) {
                Text(event.titulo)
                    .font(.title3)
                    .fontWeight(.semibold)
                    .foregroundColor(.primary)
                
                Text(event.subtitulo)
                    .font(.subheadline)
                    .foregroundColor(.secondary)
                
                HStack {
                    Text(event.hora)
                        .font(.footnote)
                        .foregroundColor(.blue) // Primary color
                    Spacer()
                    Text(event.lugar)
                        .font(.footnote)
                        .foregroundColor(.secondary)
                }
            }
            .padding(16)
            .background(Color(.systemBackground))
            .cornerRadius(12)
            .overlay(
                RoundedRectangle(cornerRadius: 12)
                    .stroke(Color.gray.opacity(0.4), lineWidth: 1)
            )
            .shadow(color: Color.black.opacity(0.1), radius: 4, x: 0, y: 2)
        }
        .buttonStyle(PlainButtonStyle()) // To remove default button styles
    }
}

// Preview with sample data
struct SectionedListScreen_Previews: PreviewProvider {
    static var previews: some View {
        SectionedListView(viewModel: EventViewModel())
            .preferredColorScheme(.light)

        SectionedListView(viewModel: EventViewModel())
            .preferredColorScheme(.dark)
    }
}
