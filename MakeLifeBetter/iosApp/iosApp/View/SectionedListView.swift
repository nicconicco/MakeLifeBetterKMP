//
//  SectionedListScreen.swift
//  iosApp
//
//  Created by Carlos Nicolau Galves on 22/01/26.
//


import SwiftUI

func getSampleEvents() -> [Event] {
    return [
        // O que tá rolando agora
        Event(
            id: "1",
            titulo: "Keynote: O Futuro da IA Generativa",
            subtitulo: "Palco Principal",
            descricao: "Descubra as últimas tendências em IA generativa e como elas estão transformando o desenvolvimento de software.",
            hora: "Agora",
            lugar: "Palco Principal",
            categoria: "agora"
        ),
        Event(
            id: "2",
            titulo: "Workshop: Compose Multiplatform",
            subtitulo: "Live Coding",
            descricao: "Aprenda a criar aplicativos multiplataforma com Compose Multiplatform em uma sessão prática.",
            hora: "Agora",
            lugar: "Sala 3B",
            categoria: "agora"
        ),
        Event(
            id: "3",
            titulo: "Espaço Networking",
            subtitulo: "Snacks liberados",
            descricao: "Conecte-se com outros desenvolvedores enquanto aproveita nossos snacks e bebidas.",
            hora: "Agora",
            lugar: "Área de Café",
            categoria: "agora"
        ),
        // Ainda vai rolar
        Event(
            id: "4",
            titulo: "Arquitetura Limpa em Escala",
            subtitulo: "Auditório Azul",
            descricao: "Como aplicar princípios de Clean Architecture em projetos de grande escala.",
            hora: "14:00",
            lugar: "Auditório Azul",
            categoria: "programado"
        ),
        Event(
            id: "5",
            titulo: "Painel: Carreira Internacional",
            subtitulo: "Palco Principal",
            descricao: "Desenvolvedores brasileiros compartilham suas experiências trabalhando no exterior.",
            hora: "15:30",
            lugar: "Palco Principal",
            categoria: "programado"
        ),
        Event(
            id: "6",
            titulo: "Happy Hour de Encerramento",
            subtitulo: "Rooftop",
            descricao: "Celebre o fim do evento com música, drinks e uma vista incrível da cidade.",
            hora: "18:00",
            lugar: "Rooftop",
            categoria: "programado"
        ),
        // Novidades
        Event(
            id: "7",
            titulo: "Lançamento da SDK v2.0",
            subtitulo: "Stand Principal",
            descricao: "Visite o stand e ganhe stickers exclusivos do lançamento.",
            hora: "Durante o evento",
            lugar: "Stand Principal",
            categoria: "novidade"
        ),
        Event(
            id: "8",
            titulo: "Hackathon Surpresa",
            subtitulo: "Recepção",
            descricao: "Inscrições abertas na recepção. Prêmios incríveis para os vencedores!",
            hora: "Inscrições abertas",
            lugar: "Recepção",
            categoria: "novidade"
        ),
        // Contatos
        Event(
            id: "9",
            titulo: "Suporte Técnico",
            subtitulo: "Ajuda disponível",
            descricao: "Precisa de ajuda com o App ou Wi-Fi? Nossa equipe está pronta para ajudar.",
            hora: "Disponível",
            lugar: "Balcão de Informações",
            categoria: "contato"
        ),
        Event(
            id: "10",
            titulo: "Discord da Comunidade",
            subtitulo: "Online",
            descricao: "Converse com outros devs agora mesmo no nosso servidor Discord.",
            hora: "24/7",
            lugar: "Online",
            categoria: "contato"
        ),
        // Cupons
        Event(
            id: "11",
            titulo: "Almoço Food Truck",
            subtitulo: "15% OFF",
            descricao: "Use o código BURGERTECH15 para ganhar 15% de desconto.",
            hora: "Válido hoje",
            lugar: "Food Trucks",
            categoria: "cupom"
        ),
        Event(
            id: "12",
            titulo: "Cursos Alura",
            subtitulo: "30% OFF",
            descricao: "30% de desconto na renovação anual. Código exclusivo para participantes.",
            hora: "Válido por 7 dias",
            lugar: "Online",
            categoria: "cupom"
        )
    ]
}

func getSampleEventSections() -> [EventSection] {
    
    let events = getSampleEvents()
    
    return [
        EventSection(
            titulo: "O que tá rolando agora",
            eventos: events.filter { $0.categoria == "agora" }
        ),
        EventSection(
            titulo: "Ainda vai rolar",
            eventos: events.filter { $0.categoria == "programado" }
        ),
        EventSection(
            titulo: "Novidades",
            eventos: events.filter { $0.categoria == "novidade" }
        ),
        EventSection(
            titulo: "Canais de Contato",
            eventos: events.filter { $0.categoria == "contato" }
        ),
        EventSection(
            titulo: "Cupons",
            eventos: events.filter { $0.categoria == "cupom" }
        )
    ]
}

struct SectionedListView: View {
    var sections: [EventSection]
    var isLoading: Bool = false
    var onItemClick: (Event) -> Void = { _ in }

    var body: some View {
        if isLoading {
            ProgressView() // Circular progress indicator
                .scaleEffect(1.5, anchor: .center) // Scale for better visibility
                .frame(maxWidth: .infinity, maxHeight: .infinity)
        } else {
            ScrollView {
                VStack(spacing: 8) {
                    ForEach(sections, id: \.titulo) { section in
                        // Section Header
                        SectionHeader(title: section.titulo)
                        
                        // Section Items
                        ForEach(section.eventos, id: \.id) { event in
                            EventCard(event: event, onClick: { onItemClick(event) })
                        }
                    }
                }
                .padding(.horizontal, 16)
                .padding(.vertical, 8)
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
        SectionedListView(sections: getSampleEventSections())
            .preferredColorScheme(.light)

        SectionedListView(sections: getSampleEventSections())
            .preferredColorScheme(.dark)
    }
}
