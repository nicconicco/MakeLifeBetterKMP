import SwiftUI

struct ChatView: View {
    var currentUsername: String
    var viewModel: ChatViewModel

    @State private var selectedTab = 0

    var body: some View {
        VStack(spacing: 0) {
            // Tab picker
            Picker("", selection: $selectedTab) {
                Text("Lista Geral").tag(0)
                Text("Duvidas").tag(1)
            }
            .pickerStyle(.segmented)
            .padding(.horizontal, 16)
            .padding(.vertical, 8)

            // Content
            switch selectedTab {
            case 0:
                GeneralChatContent(
                    messages: viewModel.messages,
                    currentUsername: currentUsername,
                    isLoading: viewModel.isChatLoading,
                    isSending: viewModel.isSending,
                    onSendMessage: { message in
                        viewModel.sendMessage(author: currentUsername, message: message)
                    }
                )
            case 1:
                if viewModel.selectedQuestion != nil {
                    QuestionDetailContent(
                        question: viewModel.selectedQuestion!,
                        replies: viewModel.replies,
                        currentUsername: currentUsername,
                        isLoading: viewModel.isRepliesLoading,
                        isAddingReply: viewModel.isAddingReply,
                        onBack: { viewModel.clearSelectedQuestion() },
                        onAddReply: { content in
                            viewModel.addReply(
                                questionId: viewModel.selectedQuestion!.id,
                                author: currentUsername,
                                content: content
                            )
                        },
                        onDeleteReply: { replyId in
                            viewModel.deleteReply(
                                questionId: viewModel.selectedQuestion!.id,
                                replyId: replyId
                            )
                        }
                    )
                } else {
                    QuestionsContent(
                        questions: viewModel.questions,
                        currentUsername: currentUsername,
                        isLoading: viewModel.isQuestionsLoading,
                        isAdding: viewModel.isAddingQuestion,
                        onAddQuestion: { title, description in
                            viewModel.addQuestion(author: currentUsername, title: title, description: description)
                        },
                        onDeleteQuestion: { questionId in
                            viewModel.deleteQuestion(questionId: questionId)
                        },
                        onQuestionClick: { question in
                            viewModel.selectQuestion(question: question)
                        }
                    )
                }
            default:
                EmptyView()
            }
        }
        .onChange(of: selectedTab) { _, newTab in
            switch newTab {
            case 0: viewModel.refreshMessages()
            case 1: viewModel.refreshQuestions()
            default: break
            }
        }
    }
}

// MARK: - General Chat

private struct GeneralChatContent: View {
    var messages: [ChatMessage]
    var currentUsername: String
    var isLoading: Bool
    var isSending: Bool
    var onSendMessage: (String) -> Void

    @State private var messageText = ""

    var body: some View {
        VStack(spacing: 0) {
            if isLoading && messages.isEmpty {
                Spacer()
                ProgressView()
                Spacer()
            } else {
                ScrollViewReader { proxy in
                    ScrollView {
                        LazyVStack(spacing: 8) {
                            ForEach(messages) { message in
                                GeneralMessageItem(
                                    message: message,
                                    isCurrentUser: message.author == currentUsername
                                )
                                .id(message.id)
                            }
                        }
                        .padding(.horizontal, 16)
                        .padding(.top, 8)
                    }
                    .onChange(of: messages.count) { _, _ in
                        if let lastId = messages.last?.id {
                            withAnimation {
                                proxy.scrollTo(lastId, anchor: .bottom)
                            }
                        }
                    }
                }
            }

            // Input bar
            HStack(spacing: 8) {
                TextField("Digite sua mensagem...", text: $messageText)
                    .textFieldStyle(.roundedBorder)
                    .disabled(isSending)
                    .onSubmit {
                        sendIfValid()
                    }

                Button(action: { sendIfValid() }) {
                    if isSending {
                        ProgressView()
                            .frame(width: 40, height: 40)
                    } else {
                        Image(systemName: "paperplane.fill")
                            .frame(width: 40, height: 40)
                            .foregroundColor(.white)
                            .background(Color.blue)
                            .clipShape(Circle())
                    }
                }
                .disabled(messageText.trimmingCharacters(in: .whitespaces).isEmpty || isSending)
            }
            .padding(8)
            .background(Color(.secondarySystemBackground))
        }
    }

    private func sendIfValid() {
        let trimmed = messageText.trimmingCharacters(in: .whitespaces)
        guard !trimmed.isEmpty, !isSending else { return }
        onSendMessage(trimmed)
        messageText = ""
    }
}

private struct GeneralMessageItem: View {
    var message: ChatMessage
    var isCurrentUser: Bool

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            HStack {
                // Avatar
                ZStack {
                    Circle()
                        .fill(isCurrentUser ? Color.blue : Color.blue.opacity(0.2))
                        .frame(width: 32, height: 32)
                    Text(String(message.author.prefix(1)))
                        .font(.caption)
                        .fontWeight(.bold)
                        .foregroundColor(isCurrentUser ? .white : .blue)
                }

                Text(isCurrentUser ? "Voce" : message.author)
                    .font(.subheadline)
                    .fontWeight(.semibold)

                Spacer()

                Text(formatTime(epochMillis: message.timestamp))
                    .font(.caption)
                    .foregroundColor(.secondary)
            }

            Text(message.message)
                .font(.body)
        }
        .padding(12)
        .background(
            RoundedRectangle(cornerRadius: 12)
                .fill(isCurrentUser ? Color.blue.opacity(0.1) : Color(.systemBackground))
        )
        .overlay(
            RoundedRectangle(cornerRadius: 12)
                .stroke(Color(.separator).opacity(0.5), lineWidth: 1)
        )
    }
}

// MARK: - Questions

private struct QuestionsContent: View {
    var questions: [Question]
    var currentUsername: String
    var isLoading: Bool
    var isAdding: Bool
    var onAddQuestion: (String, String) -> Void
    var onDeleteQuestion: (String) -> Void
    var onQuestionClick: (Question) -> Void

    @State private var showAddDialog = false

    var body: some View {
        if isLoading && questions.isEmpty {
            Spacer()
            ProgressView()
            Spacer()
        } else {
            List {
                Section {
                    Button(action: { showAddDialog = true }) {
                        Text("Fazer uma pergunta")
                            .frame(maxWidth: .infinity)
                    }
                    .buttonStyle(.borderedProminent)
                    .listRowSeparator(.hidden)
                }

                ForEach(questions) { question in
                    QuestionListItem(
                        question: question,
                        canDelete: question.author == currentUsername,
                        onDelete: { onDeleteQuestion(question.id) },
                        onClick: { onQuestionClick(question) }
                    )
                    .listRowSeparator(.hidden)
                    .listRowInsets(EdgeInsets(top: 4, leading: 16, bottom: 4, trailing: 16))
                }
            }
            .listStyle(.plain)
            .sheet(isPresented: $showAddDialog) {
                AddQuestionSheet(
                    isAdding: isAdding,
                    onDismiss: { showAddDialog = false },
                    onConfirm: { title, description in
                        onAddQuestion(title, description)
                        showAddDialog = false
                    }
                )
            }
        }
    }
}

private struct QuestionListItem: View {
    var question: Question
    var canDelete: Bool
    var onDelete: () -> Void
    var onClick: () -> Void

    @State private var showDeleteConfirmation = false

    var body: some View {
        Button(action: onClick) {
            VStack(alignment: .leading, spacing: 4) {
                HStack(alignment: .top) {
                    Text(question.title)
                        .font(.headline)
                        .fontWeight(.semibold)
                        .foregroundColor(.primary)

                    Spacer()

                    if canDelete {
                        Button(action: { showDeleteConfirmation = true }) {
                            Image(systemName: "trash")
                                .foregroundColor(.red)
                                .font(.caption)
                        }
                        .buttonStyle(.plain)
                    }
                }

                Text(question.description)
                    .font(.subheadline)
                    .foregroundColor(.secondary)
                    .lineLimit(2)

                HStack {
                    Text("Por \(question.author)")
                        .font(.caption)
                        .foregroundColor(.secondary)
                    Spacer()
                    Text("\(question.replies) respostas")
                        .font(.caption)
                        .foregroundColor(.blue)
                        .fontWeight(.medium)
                    Text(formatRelativeTime(epochMillis: question.timestamp))
                        .font(.caption)
                        .foregroundColor(.secondary)
                }
                .padding(.top, 4)
            }
            .padding(16)
            .background(Color(.systemBackground))
            .cornerRadius(12)
            .overlay(
                RoundedRectangle(cornerRadius: 12)
                    .stroke(Color(.separator), lineWidth: 1)
            )
        }
        .buttonStyle(.plain)
        .alert("Excluir pergunta", isPresented: $showDeleteConfirmation) {
            Button("Cancelar", role: .cancel) {}
            Button("Excluir", role: .destructive) { onDelete() }
        } message: {
            Text("Tem certeza que deseja excluir esta pergunta?")
        }
    }
}

private struct AddQuestionSheet: View {
    var isAdding: Bool
    var onDismiss: () -> Void
    var onConfirm: (String, String) -> Void

    @State private var title = ""
    @State private var description = ""

    var body: some View {
        NavigationStack {
            Form {
                Section("Titulo da pergunta") {
                    TextField("Titulo", text: $title)
                        .disabled(isAdding)
                }
                Section("Descricao") {
                    TextEditor(text: $description)
                        .frame(minHeight: 100)
                        .disabled(isAdding)
                }
            }
            .navigationTitle("Fazer uma pergunta")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("Cancelar") { onDismiss() }
                        .disabled(isAdding)
                }
                ToolbarItem(placement: .confirmationAction) {
                    if isAdding {
                        ProgressView()
                    } else {
                        Button("Publicar") {
                            onConfirm(title, description)
                        }
                        .disabled(title.trimmingCharacters(in: .whitespaces).isEmpty ||
                                  description.trimmingCharacters(in: .whitespaces).isEmpty)
                    }
                }
            }
        }
        .presentationDetents([.medium])
    }
}

// MARK: - Question Detail

private struct QuestionDetailContent: View {
    var question: Question
    var replies: [QuestionReply]
    var currentUsername: String
    var isLoading: Bool
    var isAddingReply: Bool
    var onBack: () -> Void
    var onAddReply: (String) -> Void
    var onDeleteReply: (String) -> Void

    @State private var replyText = ""

    var body: some View {
        VStack(spacing: 0) {
            // Header
            HStack {
                Button(action: onBack) {
                    HStack(spacing: 4) {
                        Image(systemName: "chevron.left")
                        Text("Voltar")
                    }
                }
                Spacer()
                Text("Detalhes da Pergunta")
                    .font(.headline)
                    .fontWeight(.bold)
                Spacer()
                // Balance spacer
                HStack(spacing: 4) {
                    Image(systemName: "chevron.left")
                    Text("Voltar")
                }
                .opacity(0)
            }
            .padding(.horizontal, 16)
            .padding(.vertical, 12)
            .background(Color(.systemBackground))

            // Content
            ScrollViewReader { proxy in
                ScrollView {
                    VStack(spacing: 12) {
                        // Question card
                        VStack(alignment: .leading, spacing: 8) {
                            Text(question.title)
                                .font(.title3)
                                .fontWeight(.bold)
                            Text(question.description)
                                .font(.body)
                                .foregroundColor(.secondary)
                            HStack {
                                Text("Por \(question.author)")
                                    .font(.caption)
                                    .fontWeight(.medium)
                                    .foregroundColor(.blue)
                                Spacer()
                                Text(formatRelativeTime(epochMillis: question.timestamp))
                                    .font(.caption)
                                    .foregroundColor(.secondary)
                            }
                            .padding(.top, 4)
                        }
                        .frame(maxWidth: .infinity, alignment: .leading)
                        .padding(16)
                        .background(Color.blue.opacity(0.1))
                        .overlay(
                            RoundedRectangle(cornerRadius: 12)
                                .stroke(Color.blue, lineWidth: 1)
                        )
                        .cornerRadius(12)

                        // Replies header
                        Text("Respostas (\(replies.count))")
                            .font(.headline)
                            .fontWeight(.semibold)
                            .frame(maxWidth: .infinity, alignment: .leading)
                            .padding(.top, 8)

                        // Replies
                        if isLoading && replies.isEmpty {
                            ProgressView()
                                .padding(32)
                        } else if replies.isEmpty {
                            Text("Nenhuma resposta ainda. Seja o primeiro a responder!")
                                .font(.body)
                                .foregroundColor(.secondary)
                                .padding(32)
                                .frame(maxWidth: .infinity)
                        } else {
                            ForEach(replies) { reply in
                                ReplyItem(
                                    reply: reply,
                                    canDelete: reply.author == currentUsername,
                                    onDelete: { onDeleteReply(reply.id) }
                                )
                                .id(reply.id)
                            }
                        }
                    }
                    .padding(16)
                }
                .onChange(of: replies.count) { _, _ in
                    if let lastId = replies.last?.id {
                        withAnimation {
                            proxy.scrollTo(lastId, anchor: .bottom)
                        }
                    }
                }
            }

            // Reply input
            HStack(spacing: 8) {
                TextField("Escreva sua resposta...", text: $replyText)
                    .textFieldStyle(.roundedBorder)
                    .disabled(isAddingReply)
                    .onSubmit { sendReplyIfValid() }

                Button(action: { sendReplyIfValid() }) {
                    if isAddingReply {
                        ProgressView()
                            .frame(width: 40, height: 40)
                    } else {
                        Image(systemName: "paperplane.fill")
                            .frame(width: 40, height: 40)
                            .foregroundColor(.white)
                            .background(Color.blue)
                            .clipShape(Circle())
                    }
                }
                .disabled(replyText.trimmingCharacters(in: .whitespaces).isEmpty || isAddingReply)
            }
            .padding(8)
            .background(Color(.secondarySystemBackground))
        }
    }

    private func sendReplyIfValid() {
        let trimmed = replyText.trimmingCharacters(in: .whitespaces)
        guard !trimmed.isEmpty, !isAddingReply else { return }
        onAddReply(trimmed)
        replyText = ""
    }
}

private struct ReplyItem: View {
    var reply: QuestionReply
    var canDelete: Bool
    var onDelete: () -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            HStack {
                // Avatar
                ZStack {
                    Circle()
                        .fill(Color.blue.opacity(0.2))
                        .frame(width: 32, height: 32)
                    Text(String(reply.author.prefix(1)))
                        .font(.caption)
                        .fontWeight(.bold)
                        .foregroundColor(.blue)
                }

                VStack(alignment: .leading) {
                    Text(reply.author)
                        .font(.subheadline)
                        .fontWeight(.semibold)
                    Text(formatRelativeTime(epochMillis: reply.timestamp))
                        .font(.caption)
                        .foregroundColor(.secondary)
                }

                Spacer()

                if canDelete {
                    Button(action: onDelete) {
                        Image(systemName: "xmark")
                            .foregroundColor(.red)
                            .font(.caption)
                    }
                    .buttonStyle(.plain)
                }
            }

            Text(reply.content)
                .font(.body)
        }
        .padding(12)
        .background(Color(.systemBackground))
        .cornerRadius(12)
        .overlay(
            RoundedRectangle(cornerRadius: 12)
                .stroke(Color(.separator).opacity(0.5), lineWidth: 1)
        )
    }
}
